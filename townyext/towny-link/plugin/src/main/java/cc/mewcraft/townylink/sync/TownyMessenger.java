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
import me.lucko.helper.terminable.Terminable;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.UUID;

/**
 * This class manages the Global Data of Towny.
 */
@Singleton
public class TownyMessenger implements Terminable {
    private final TownyLinkPlugin plugin;
    private final GlobalDataHolder globalData;

    private final Channel<NewGovernmentPacket> newChannel;
    private final Channel<DeleteGovernmentPacket> deleteChannel;
    private final Channel<InitialGovernmentPacket> initChannel;

    private final ChannelAgent<NewGovernmentPacket> newAgent;
    private final ChannelAgent<DeleteGovernmentPacket> deleteAgent;
    private final ChannelAgent<InitialGovernmentPacket> initAgent;

    @Inject
    public TownyMessenger(final TownyLinkPlugin plugin, final GlobalDataHolder globalData) {
        this.plugin = plugin;
        this.globalData = globalData;
        Messenger messenger = plugin.getService(Messenger.class);
        this.initChannel = messenger.getChannel("init-government", InitialGovernmentPacket.class);
        this.newChannel = messenger.getChannel("new-government", NewGovernmentPacket.class);
        this.deleteChannel = messenger.getChannel("delete-government", DeleteGovernmentPacket.class);
        this.initAgent = initChannel.newAgent();
        this.newAgent = newChannel.newAgent();
        this.deleteAgent = deleteChannel.newAgent();

        // --- Register listeners ---

        initAgent.addListener((agent, message) -> {
            String currentServer = ServerInfo.SERVER_ID.get();
            if (Objects.equals(currentServer, message.sender())) {
                return; // message comes from current server - ignore it
            }

            switch (message.type()) {
                case TOWN -> message.data().forEach(globalData::putTown);
                case NATION -> message.data().forEach(globalData::putNation);
            }
        });

        newAgent.addListener((agent, message) -> {
            String currentServer = ServerInfo.SERVER_ID.get();
            if (Objects.equals(currentServer, message.sender())) {
                return; // message comes from current server - ignore it
            }

            switch (message.type()) {
                case TOWN -> globalData.putTown(new GovernmentObject(message.uuid(), message.name()));
                case NATION -> globalData.putNation(new GovernmentObject(message.uuid(), message.name()));
            }
        });

        deleteAgent.addListener((agent, message) -> {
            String currentServer = ServerInfo.SERVER_ID.get();
            if (Objects.equals(currentServer, message.sender())) {
                return; // message comes from current server - ignore it
            }

            switch (message.type()) {
                case TOWN -> globalData.removeTown(message.uuid());
                case NATION -> globalData.removeNation(message.uuid());
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
                data.stream().map(t -> new GovernmentObject(t.id(), t.name())).forEach(globalData::putTown);
                plugin.getSLF4JLogger().info("Received total {} towns from other servers", data.size());
            })

            // Request nations from other servers
            .thenComposeAsync(n -> TownyLinkProvider.get().requestGlobalNation())
            .thenAcceptAsync(data -> {
                data.stream().map(t -> new GovernmentObject(t.id(), t.name())).forEach(globalData::putNation);
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
        newChannel.sendMessage(packet);
    }

    void broadcastDelete(GovernmentType type, @NonNull UUID uuid) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(uuid);

        String sender = ServerInfo.SERVER_ID.get();
        DeleteGovernmentPacket packet = new DeleteGovernmentPacket(sender, uuid, type);
        plugin.getSLF4JLogger().info("Broadcast government update - {}", packet);
        deleteChannel.sendMessage(packet);
    }

    void broadcastExisting(@NonNull GovernmentType type, ImmutableSet<GovernmentObject> data) {
        Objects.requireNonNull(type);

        String sender = ServerInfo.SERVER_ID.get();
        InitialGovernmentPacket packet = new InitialGovernmentPacket(sender, type, data);
        plugin.getSLF4JLogger().info("Broadcast {} {} to the network - {}", data.size(), type.name().toLowerCase() + "(s)", packet);
        initChannel.sendMessage(packet);
    }

    @Override public void close() {
        initAgent.close();
        newAgent.close();
        deleteAgent.close();
    }
}
