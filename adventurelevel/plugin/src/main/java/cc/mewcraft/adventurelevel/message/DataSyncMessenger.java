package cc.mewcraft.adventurelevel.message;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import cc.mewcraft.adventurelevel.data.PlayerData;
import cc.mewcraft.adventurelevel.level.category.LevelBean;
import cc.mewcraft.adventurelevel.util.PlayerUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.lucko.helper.messaging.Channel;
import me.lucko.helper.messaging.ChannelAgent;
import me.lucko.helper.messaging.util.MappedChannelReceiver;
import me.lucko.helper.redis.Redis;
import me.lucko.helper.terminable.Terminable;
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
@Singleton
public class DataSyncMessenger implements Terminable {

    private static final String DATA_SYNC_CHANNEL = "adventure-level-data-sync";

    private final AdventureLevelPlugin plugin;
    private final Channel<TransientPlayerData> channel;
    private final ChannelAgent<TransientPlayerData> debuggingAgent;
    private final MappedChannelReceiver<TransientPlayerData, UUID, TransientPlayerData> messageStore;

    @Inject
    public DataSyncMessenger(
        final AdventureLevelPlugin plugin,
        final Redis redis
    ) {
        this.plugin = plugin;

        // Get and define the channel.
        this.channel = redis.getChannel(DATA_SYNC_CHANNEL, TransientPlayerData.class);

        this.debuggingAgent = this.channel.newAgent((agent, message) -> {
            // Log received message for debugging
            plugin.getSLF4JLogger().info("Received userdata from channel: name={},uuid={},mainXp={}",
                PlayerUtils.getNameFromUUID(message.uuid()),
                message.uuid(),
                message.mainXp()
            );
        });

        // This acts as a listener that stores the PlayerData sent on the channel.
        this.messageStore = MappedChannelReceiver.createExpiring(
            channel,
            TransientPlayerData::uuid, // the key - which is extracted from the received message
            Function.identity(), // the value - do not transform it
            10,
            TimeUnit.SECONDS
        );
    }

    /**
     * This method should be called upon player quitting the server.
     *
     * @param data the player data to be sent to the channel
     */
    public void send(@NotNull PlayerData data) {
        channel.sendMessage(new TransientPlayerData(
            data.getUuid(),
            data.getMainLevel().getExperience(),
            data.getCateLevel(LevelBean.Category.BLOCK_BREAK).getExperience(),
            data.getCateLevel(LevelBean.Category.BREED).getExperience(),
            data.getCateLevel(LevelBean.Category.ENTITY_DEATH).getExperience(),
            data.getCateLevel(LevelBean.Category.EXP_BOTTLE).getExperience(),
            data.getCateLevel(LevelBean.Category.FISHING).getExperience(),
            data.getCateLevel(LevelBean.Category.FURNACE).getExperience(),
            data.getCateLevel(LevelBean.Category.GRINDSTONE).getExperience(),
            data.getCateLevel(LevelBean.Category.PLAYER_DEATH).getExperience(),
            data.getCateLevel(LevelBean.Category.VILLAGER_TRADE).getExperience()
        )).thenAcceptAsync(n -> {
            plugin.getSLF4JLogger().info("Sent userdata to channel: name={},uuid={},mainXp={}",
                PlayerUtils.getNameFromUUID(data.getUuid()),
                data.getUuid(),
                data.getMainLevel().getExperience()
            );
        });
    }

    /**
     * Gets the cached player data in the message store.
     *
     * @return the PlayerData cached in the message store, or null if it's not cached
     */
    public @Nullable TransientPlayerData get(UUID uuid) {
        return messageStore.getValue(uuid);
    }

    @Override public void close() throws Exception {
        messageStore.close();
        debuggingAgent.close();
    }

}
