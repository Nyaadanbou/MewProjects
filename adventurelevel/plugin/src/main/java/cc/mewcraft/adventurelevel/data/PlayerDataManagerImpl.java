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
import com.google.common.cache.RemovalNotification;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.lucko.helper.promise.Promise;
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
    private final LoadingCache<UUID, Promise<PlayerData>> loadingCache = CacheBuilder.newBuilder()
        .expireAfterAccess(Duration.of(3, ChronoUnit.SECONDS)) // TODO remove when debug is done
        .removalListener(new PlayerDataRemovalListener())
        .build(new PlayerDataLoader());

    // --- Config settings ---
    private final int networkLatencyMilliseconds;

    /**
     * Methods define how the data is fetched.
     */
    private class PlayerDataLoader extends CacheLoader<UUID, Promise<PlayerData>> {
        @Override public @NotNull Promise<PlayerData> load(
            final @NotNull UUID key
        ) throws Exception {
            return Promise

                // Get an empty PlayerData.
                .supplyingAsync(() -> new RealPlayerData(plugin, key))

                // Create a callback to update its states.
                .thenApplyDelayedAsync(data -> {

                    // Get data from message store first
                    PlayerDataPacket message = messenger.get(key);
                    if (message != null && !data.complete()) {
                        plugin.getSLF4JLogger().info("Fully loaded userdata from message store: name={}, mainXp={}", PlayerUtils.getNameFromUUID(key), message.mainXp());
                        return PlayerDataUpdater.update(data, message).markAsComplete();
                    }

                    // The message store does not have the data,
                    // so load the data from disk and return it.
                    PlayerData fromDisk = storage.load(key);
                    if (fromDisk.equals(PlayerData.DUMMY)) {
                        fromDisk = storage.create(key); // Not existing in disk - create one
                    }
                    return PlayerDataUpdater.update(data, fromDisk).markAsComplete();

                    // The delay is necessary to allow the message
                    // to be published and received on the network.
                }, networkLatencyMilliseconds, TimeUnit.MILLISECONDS);
        }

        /**
         * Not used yet.
         */
        @Override public @NotNull ListenableFuture<Promise<PlayerData>> reload(
            final @NotNull UUID key,
            final @NotNull Promise<PlayerData> oldValue
        ) {
            Promise<PlayerData> promise = oldValue.thenApplyAsync(oldData -> {
                // We need to keep the old reference but update the internal states.
                // This ensures that there is only one instance actually being used.
                return PlayerDataUpdater.update(oldData, storage.load(key)).markAsComplete();
            });
            return Futures.immediateFuture(promise);
        }
    }

    /**
     * Methods define the callback when an entry is removed from the cache.
     */
    private class PlayerDataRemovalListener implements RemovalListener<UUID, Promise<PlayerData>> {
        @Override public void onRemoval(final RemovalNotification<UUID, Promise<PlayerData>> notification) {
            Objects.requireNonNull(notification.getValue())

                // We need to save to file in case the entry is evicted and then lost.
                .thenComposeAsync(PlayerDataManagerImpl.this::save)

                .thenAcceptAsync(data -> plugin.getSLF4JLogger().info(
                    "Unloaded userdata from cache: name={}, mainXp={}",
                    PlayerUtils.getNameFromUUID(data.getUuid()), data.getLevelBean(LevelCategory.MAIN).getExperience())
                );
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
        this.networkLatencyMilliseconds = Math.max(0, plugin.getConfig().getInt("synchronization.network_latency_milliseconds"));
    }

    @Override public @NotNull Map<UUID, Promise<PlayerData>> asMap() {
        return loadingCache.asMap();
    }

    @Override public @NotNull Promise<PlayerData> load(final @NotNull UUID uuid) {
        return loadingCache.getUnchecked(uuid);
    }

    @Override public @NotNull Promise<PlayerData> save(final @NotNull PlayerData playerData) {
        return !playerData.complete()
            ? Promise.completed(playerData)
            : Promise.supplyingAsync(() -> {
                storage.save(playerData);
                return playerData;
            });
    }

    @Override public @NotNull Promise<UUID> unload(final @NotNull UUID uuid) {
        loadingCache.invalidate(uuid);
        return Promise.completed(uuid);
    }

    @Override public void refresh(final @NotNull UUID uuid) {
        loadingCache.refresh(uuid);
    }

    @Override public void close() {
        // We need to save all ONLINE players data before shutdown.
        // Doing so we can safely and completely reload the plugin.
        for (final Promise<PlayerData> value : loadingCache.asMap().values()) {
            value.thenAcceptSync(playerData -> {
                if (Players.get(playerData.getUuid()).isPresent()) storage.save(playerData);
            });
        }
    }
}
