package cc.mewcraft.adventurelevel.message;


import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import cc.mewcraft.adventurelevel.data.PlayerData;
import cc.mewcraft.adventurelevel.data.RealPlayerData;
import com.google.inject.Inject;
import me.lucko.helper.messaging.Channel;
import me.lucko.helper.messaging.Messenger;
import me.lucko.helper.messaging.util.MappedChannelReceiver;
import me.lucko.helper.terminable.Terminable;
import me.lucko.helper.utils.Players;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * This class provides methods to sync data between servers.
 * <p>
 * Why we have this class? Because SQL database is too slow to save/load data when players switch servers.
 */
public class DataSyncMessenger implements Terminable {

    private static final String DATA_SYNC_CHANNEL = "adventure-level-data-sync";

    private final AdventureLevelPlugin plugin;
    private final Channel<RealPlayerData> channel;
    private final MappedChannelReceiver<RealPlayerData, UUID, RealPlayerData> messageStore;

    @Inject
    public DataSyncMessenger(
        final AdventureLevelPlugin plugin,
        final Messenger messenger
    ) {
        this.plugin = plugin;

        // Get and define the channel.
        this.channel = messenger.getChannel(DATA_SYNC_CHANNEL, RealPlayerData.class);

        // This acts as a listener that stores the PlayerData sent on the channel.
        this.messageStore = MappedChannelReceiver.createExpiring(
            channel,
            PlayerData::getUuid, // the key - which is extracted from the received message
            Function.identity(), // the value - let it be as it is
            30, // it's unlikely that a player takes more than 30 seconds to switch servers
            TimeUnit.SECONDS
        );
    }

    /**
     * This method should be called upon player quitting the server.
     *
     * @param playerData the player data to be sent to the channel
     */
    public void sendData(@NotNull PlayerData playerData) {
        if (playerData instanceof RealPlayerData data) {
            channel.sendMessage(data);
            plugin.getSLF4JLogger().info("Sent userdata to channel: {} ({})", Players.getOffline(playerData.getUuid()).map(OfflinePlayer::getName).orElse("Null"), playerData.getUuid());
        }
    }

    /**
     * Gets the cached player data in the message store.
     *
     * @return the PlayerData cached in the message store, or null if it's not cached
     */
    public @Nullable PlayerData getData(UUID uuid) {
        return messageStore.getValue(uuid);
    }

    @Override public void close() {
        messageStore.close();
    }

}
