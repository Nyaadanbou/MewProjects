package cc.mewcraft.townylink.sync;

import cc.mewcraft.mewcore.network.ServerInfo;
import cc.mewcraft.townylink.TownyLinkPlugin;
import cc.mewcraft.townylink.api.TownyLinkProvider;
import cc.mewcraft.townylink.sync.local.GlobalDataHolder;
import cc.mewcraft.townylink.sync.local.GovernmentObject;
import cc.mewcraft.townylink.sync.packet.DeleteGovernmentPacket;
import cc.mewcraft.townylink.sync.packet.GovernmentType;
import cc.mewcraft.townylink.sync.packet.InitialGovernmentPacket;
import cc.mewcraft.townylink.sync.packet.NewGovernmentPacket;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.palmergames.bukkit.towny.TownyAPI;
import me.lucko.helper.messaging.Channel;
import me.lucko.helper.messaging.ChannelAgent;
import me.lucko.helper.messaging.Messenger;
import me.lucko.helper.promise.Promise;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * This class manages the Global Data of Towny.
 */
@Singleton
public class TownyMessenger implements TerminableModule {
    private final TownyLinkPlugin plugin;
    private final GlobalDataHolder holder;

    private final Channel<NewGovernmentPacket> creationChannel;
    private final Channel<DeleteGovernmentPacket> deletionChannel;
    private final Channel<InitialGovernmentPacket> initializationChannel;

    private final ChannelAgent<NewGovernmentPacket> creationAgent;
    private final ChannelAgent<DeleteGovernmentPacket> deletionAgent;
    private final ChannelAgent<InitialGovernmentPacket> initializationAgent;

    @Inject
    public TownyMessenger(final TownyLinkPlugin plugin, final GlobalDataHolder holder) {
        this.plugin = plugin;
        this.holder = holder;

        Messenger messenger = plugin.getService(Messenger.class);

        this.creationChannel = messenger.getChannel("new-government", NewGovernmentPacket.class);
        this.deletionChannel = messenger.getChannel("delete-government", DeleteGovernmentPacket.class);
        this.initializationChannel = messenger.getChannel("init-government", InitialGovernmentPacket.class);

        this.creationAgent = creationChannel.newAgent();
        this.deletionAgent = deletionChannel.newAgent();
        this.initializationAgent = initializationChannel.newAgent();
    }

    @Override public void setup(final @NotNull TerminableConsumer consumer) {
        consumer.bind(creationAgent);
        consumer.bind(deletionAgent);
        consumer.bind(initializationAgent);

        // --- Register listeners ---

        initializationAgent.addListener((agent, message) -> {
            String currentServer = ServerInfo.SERVER_ID.get();
            if (Objects.equals(currentServer, message.sender())) {
                return; // message comes from current server - ignore it
            }

            switch (message.type()) {
                case TOWN -> message.data().forEach(holder::putTown);
                case NATION -> message.data().forEach(holder::putNation);
            }
        });

        creationAgent.addListener((agent, message) -> {
            String currentServer = ServerInfo.SERVER_ID.get();
            if (Objects.equals(currentServer, message.sender())) {
                return; // message comes from current server - ignore it
            }

            switch (message.type()) {
                case TOWN -> holder.putTown(new GovernmentObject(message.uuid(), message.name()));
                case NATION -> holder.putNation(new GovernmentObject(message.uuid(), message.name()));
            }
        });

        deletionAgent.addListener((agent, message) -> {
            String currentServer = ServerInfo.SERVER_ID.get();
            if (Objects.equals(currentServer, message.sender())) {
                return; // message comes from current server - ignore it
            }

            switch (message.type()) {
                case TOWN -> holder.removeTown(message.uuid());
                case NATION -> holder.removeNation(message.uuid());
            }
        });
    }

    void sync() {
        Promise.start()

            // Report start of the process
            .thenRunAsync(() -> plugin.getSLF4JLogger().info("Starting global data synchronization process ..."))

            // Broadcast towns and nations from this server to the network
            .thenRunAsync(() -> {
                ImmutableSet.Builder<GovernmentObject> townData = ImmutableSet.builder();
                TownyAPI.getInstance().getTowns().forEach(town -> townData.add(new GovernmentObject(town.getUUID(), town.getName())));
                broadcastExisting(GovernmentType.TOWN, townData.build());
            })
            .thenRunAsync(() -> {
                ImmutableSet.Builder<GovernmentObject> nationData = ImmutableSet.builder();
                TownyAPI.getInstance().getNations().forEach(nation -> nationData.add(new GovernmentObject(nation.getUUID(), nation.getName())));
                broadcastExisting(GovernmentType.NATION, nationData.build());
            })

            // Request towns from other servers
            .thenComposeAsync(n -> TownyLinkProvider.get().requestGlobalTown())
            .thenAcceptAsync(data -> {
                data.stream().map(t -> new GovernmentObject(t.id(), t.name())).forEach(holder::putTown);
                plugin.getSLF4JLogger().info("Received total {} towns from other servers", data.size());
            })

            // Request nations from other servers
            .thenComposeAsync(n -> TownyLinkProvider.get().requestGlobalNation())
            .thenAcceptAsync(data -> {
                data.stream().map(t -> new GovernmentObject(t.id(), t.name())).forEach(holder::putNation);
                plugin.getSLF4JLogger().info("Received total {} nations from other servers", data.size());
            })

            // Report end of the process
            .thenRunAsync(() -> plugin.getSLF4JLogger().info("Synchronization process completed! Any errors have been reported above."));
    }

    void broadcastNew(@NonNull GovernmentType type, @NonNull UUID uuid, @NonNull String name) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(uuid);
        Objects.requireNonNull(name);

        String sender = ServerInfo.SERVER_ID.get();
        NewGovernmentPacket packet = new NewGovernmentPacket(sender, uuid, name, type);
        plugin.getSLF4JLogger().info("Broadcast government update - {}", packet);
        creationChannel.sendMessage(packet);
    }

    void broadcastDelete(GovernmentType type, @NonNull UUID uuid) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(uuid);

        String sender = ServerInfo.SERVER_ID.get();
        DeleteGovernmentPacket packet = new DeleteGovernmentPacket(sender, uuid, type);
        plugin.getSLF4JLogger().info("Broadcast government update - {}", packet);
        deletionChannel.sendMessage(packet);
    }

    void broadcastExisting(@NonNull GovernmentType type, ImmutableSet<GovernmentObject> data) {
        Objects.requireNonNull(type);

        String sender = ServerInfo.SERVER_ID.get();
        InitialGovernmentPacket packet = new InitialGovernmentPacket(sender, type, data);
        plugin.getSLF4JLogger().info("Broadcast {} {} to the network - {}", data.size(), type.name().toLowerCase() + "(s)", packet);
        initializationChannel.sendMessage(packet);
    }
}
