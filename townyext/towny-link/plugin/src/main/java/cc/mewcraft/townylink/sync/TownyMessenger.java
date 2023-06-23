package cc.mewcraft.townylink.sync;

import cc.mewcraft.mewcore.network.ServerInfo;
import cc.mewcraft.townylink.TownyLinkPlugin;
import cc.mewcraft.townylink.sync.local.GlobalDataHolder;
import cc.mewcraft.townylink.sync.local.GovernmentObject;
import cc.mewcraft.townylink.sync.packet.DeleteGovernmentPacket;
import cc.mewcraft.townylink.sync.packet.GovernmentType;
import cc.mewcraft.townylink.sync.packet.InitialGovernmentPacket;
import cc.mewcraft.townylink.sync.packet.NewGovernmentPacket;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.lucko.helper.messaging.Channel;
import me.lucko.helper.messaging.ChannelAgent;
import me.lucko.helper.messaging.Messenger;
import me.lucko.helper.promise.Promise;
import me.lucko.helper.terminable.Terminable;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.UUID;

/**
 * This class manages the Towny Global Data channel.
 */
@Singleton
public class TownyMessenger implements Terminable {
    private final Channel<NewGovernmentPacket> newChannel;
    private final Channel<DeleteGovernmentPacket> deleteChannel;
    private final Channel<InitialGovernmentPacket> initChannel;
    private final ChannelAgent<NewGovernmentPacket> newAgent;
    private final ChannelAgent<DeleteGovernmentPacket> deleteAgent;
    private final ChannelAgent<InitialGovernmentPacket> initAgent;

    @Inject
    public TownyMessenger(final TownyLinkPlugin plugin, final GlobalDataHolder globalData) {
        Messenger messenger = plugin.getService(Messenger.class);
        this.newChannel = messenger.getChannel("new-government", NewGovernmentPacket.class);
        this.deleteChannel = messenger.getChannel("delete-government", DeleteGovernmentPacket.class);
        this.initChannel = messenger.getChannel("init-government", InitialGovernmentPacket.class);
        this.newAgent = newChannel.newAgent();
        this.deleteAgent = deleteChannel.newAgent();
        this.initAgent = initChannel.newAgent();

        // --- Register listeners ---

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
    }

    @NonNull Promise<Void> broadcastNew(@NonNull GovernmentType type, @NonNull UUID uuid, @NonNull String name) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(uuid);
        Objects.requireNonNull(name);

        String sender = ServerInfo.SERVER_ID.get();
        return Promise.start().thenRunAsync(() -> newChannel.sendMessage(new NewGovernmentPacket(sender, uuid, name, type)));
    }

    @NonNull Promise<Void> broadcastDelete(GovernmentType type, @NonNull UUID uuid) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(uuid);

        String sender = ServerInfo.SERVER_ID.get();
        return Promise.start().thenRunAsync(() -> deleteChannel.sendMessage(new DeleteGovernmentPacket(sender, uuid, type)));
    }

    @NonNull Promise<Void> broadcastExisting(@NonNull GovernmentType type, ImmutableSet<GovernmentObject> data) {
        Objects.requireNonNull(type);

        String sender = ServerInfo.SERVER_ID.get();
        return Promise.start().thenRunAsync(() -> initChannel.sendMessage(new InitialGovernmentPacket(sender, type, data)));
    }

    @Override public void close() {
        newAgent.close();
        deleteAgent.close();
        initAgent.close();
    }
}
