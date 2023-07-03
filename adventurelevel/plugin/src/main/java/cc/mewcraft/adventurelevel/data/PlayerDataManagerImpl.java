package cc.mewcraft.adventurelevel.data;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import cc.mewcraft.adventurelevel.file.DataStorage;
import cc.mewcraft.adventurelevel.level.category.LevelCategory;
import cc.mewcraft.adventurelevel.message.PlayerDataMessenger;
import cc.mewcraft.adventurelevel.message.packet.PlayerDataPacket;
import cc.mewcraft.adventurelevel.util.PlayerUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalListeners;
import com.google.common.cache.RemovalNotification;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.lucko.helper.Schedulers;
import me.lucko.helper.promise.Promise;
import me.lucko.helper.scheduler.HelperExecutors;
import me.lucko.helper.utils.Players;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Singleton
public class PlayerDataManagerImpl implements PlayerDataManager {
    private final AdventureLevelPlugin plugin;
    private final DataStorage storage;
    private final PlayerDataMessenger messenger;
    private final LoadingCache<UUID, PlayerData> loadingCache = CacheBuilder.newBuilder()
        .expireAfterAccess(Duration.of(5, ChronoUnit.MINUTES))
        .removalListener(RemovalListeners.asynchronous(new PlayerDataRemovalListener(), HelperExecutors.asyncHelper()))
        .build(new PlayerDataLoader());

    // --- Config settings ---
    private final long networkLatencyMilliseconds;

    private class PlayerDataLoader extends CacheLoader<UUID, PlayerData> {
        @Override public @NotNull PlayerData load(
            final @NotNull UUID key
        ) {
            RealPlayerData data = new RealPlayerData(plugin, key);

            Schedulers.builder()
                .async()
                .after(networkLatencyMilliseconds, TimeUnit.MILLISECONDS)
                .run(() -> {

                    if (data.complete()) {
                        return; // It is already complete - do nothing
                    }

                    // Get data from message store first
                    PlayerDataPacket message = messenger.get(key);
                    if (message != null) {
                        plugin.getSLF4JLogger().info("Fully loaded userdata from message store: name={}, mainXp={}", PlayerUtils.getNameFromUUID(key), message.mainXp());
                        PlayerDataUpdater.update(data, message).markAsComplete();
                        return;
                    }

                    // The message store does not have the data,
                    // so load the data from file and return it.
                    PlayerData fromFile = storage.load(key);
                    if (fromFile.equals(PlayerData.DUMMY)) {
                        fromFile = storage.create(key); // Not existing in disk - create one
                    }
                    PlayerDataUpdater.update(data, fromFile).markAsComplete();

                });

            return data;
        }
    }

    /**
     * Methods define the callback when an entry is removed from the cache.
     */
    private class PlayerDataRemovalListener implements RemovalListener<UUID, PlayerData> {
        @Override public void onRemoval(final RemovalNotification<UUID, PlayerData> notification) {
            PlayerData data = notification.getValue();
            Objects.requireNonNull(data, "data");

            // We need to save it into file in case the entry is evicted and then lost.
            storage.save(data);

            plugin.getSLF4JLogger().info("Saved and unloaded userdata: name={}, mainXp={}", PlayerUtils.getNameFromUUID(data.getUuid()), data.getLevel(LevelCategory.MAIN).getExperience());
        }
    }

    @Inject
    public PlayerDataManagerImpl(
        final AdventureLevelPlugin plugin,
        final DataStorage storage,
        final PlayerDataMessenger messenger
    ) {
        this.plugin = plugin;
        this.storage = storage;
        this.messenger = messenger;

        // Load config settings
        this.networkLatencyMilliseconds = Math.max(0, plugin.getConfig().getLong("synchronization.network_latency_milliseconds"));
    }

    @Override public @NotNull Map<UUID, PlayerData> asMap() {
        return loadingCache.asMap();
    }

    @Override public @NotNull PlayerData load(final @NotNull UUID uuid) {
        return loadingCache.getUnchecked(uuid);
    }

    @Override public @NotNull Promise<PlayerData> save(final @NotNull PlayerData playerData) {
        return !playerData.complete()
            ? Promise.supplyingExceptionallyAsync(() -> playerData)
            : Promise.supplyingAsync(() -> {
                storage.save(playerData);
                return playerData;
            });
    }

    @Override public @NotNull UUID unload(final @NotNull UUID uuid) {
        loadingCache.invalidate(uuid);
        return uuid;
    }

    @Override public void refresh(final @NotNull UUID uuid) {
        loadingCache.refresh(uuid);
    }

    @Override public void close() {
        // We need to save all ONLINE players data before shutdown.
        // Doing so we can safely and completely reload the plugin.
        for (final PlayerData value : loadingCache.asMap().values()) {
            if (Players.get(value.getUuid()).isPresent()) storage.save(value);
        }
    }
}
