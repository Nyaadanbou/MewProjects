package cc.mewcraft.adventurelevel.data;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import cc.mewcraft.adventurelevel.file.DataStorage;
import cc.mewcraft.adventurelevel.message.DataSyncMessenger;
import cc.mewcraft.adventurelevel.message.TransientPlayerData;
import cc.mewcraft.adventurelevel.util.PlayerUtils;
import com.google.common.cache.*;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import me.lucko.helper.promise.Promise;
import me.lucko.helper.utils.Players;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerDataManagerImpl implements PlayerDataManager {
    private final AdventureLevelPlugin plugin;
    private final DataStorage storage;
    private final DataSyncMessenger messenger;
    private final LoadingCache<UUID, Promise<PlayerData>> loadingCache = CacheBuilder.newBuilder()
        .expireAfterAccess(Duration.of(5, ChronoUnit.MINUTES))
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
                // The delay is necessary to allow the message
                // to be published and received on the network.
                .thenApplyDelayedAsync(data -> {
                    if (Players.get(data.getUuid())
                        .map(player -> !player.isOnline())
                        .orElse(true)) {
                        // Do not load the data for "ghost join"
                        plugin.getSLF4JLogger().info("Cancelled post load: name={},uuid={}",
                            PlayerUtils.getNameFromUUID(key),
                            data.getUuid()
                        );
                        return data;
                    }

                    // Get data from message store first
                    TransientPlayerData message = messenger.get(key);
                    if (message != null) {
                        plugin.getSLF4JLogger().info("Loaded userdata from message store: name={},uuid={},mainXp={}",
                            PlayerUtils.getNameFromUUID(key),
                            message.uuid(),
                            message.mainXp()
                        );
                        return PlayerDataUpdater.update(data, message).markAsComplete();
                    }

                    // The message store does not have the data,
                    // so load the data from disk and return it
                    PlayerData fromDisk = storage.load(key);
                    if (fromDisk.equals(PlayerData.DUMMY)) {
                        fromDisk = storage.create(key); // Not existing in disk - so create one
                    }
                    return PlayerDataUpdater.update(data, fromDisk).markAsComplete();
                }, networkLatencyMilliseconds, TimeUnit.MILLISECONDS);
        }

        /**
         * Not used yet.
         */
        @Override public @NotNull ListenableFuture<Promise<PlayerData>> reload(
            final @NotNull UUID key,
            final @NotNull Promise<PlayerData> oldValue
        ) throws Exception {
            Promise<PlayerData> playerDataPromise = oldValue
                .thenApplyAsync(oldData -> {
                    PlayerData newData = storage.load(key);

                    // We need to keep the old reference but update the internal states.
                    // This ensures that there is only one instance actually being used.
                    PlayerDataUpdater.update(oldData, newData);

                    return oldData;
                });
            return Futures.immediateFuture(playerDataPromise);
        }
    }

    /**
     * Methods define the callback when an entry is removed from the cache.
     */
    private class PlayerDataRemovalListener implements RemovalListener<UUID, Promise<PlayerData>> {
        @Override public void onRemoval(final RemovalNotification<UUID, Promise<PlayerData>> notification) {
            Objects.requireNonNull(notification.getValue()).thenAcceptAsync(playerData -> {
                if (playerData.complete()) {
                    messenger.send(playerData); // publish it on the network
                    storage.save(playerData); // save to disk
                    plugin.getSLF4JLogger().info("Unloaded userdata from cache: name={},uuid={},mainXp={}",
                        PlayerUtils.getNameFromUUID(playerData.getUuid()),
                        playerData.getUuid(),
                        playerData.getMainLevel().getExperience()
                    );
                }
            });
        }
    }

    @Inject
    public PlayerDataManagerImpl(
        final AdventureLevelPlugin plugin,
        final DataStorage storage,
        final DataSyncMessenger messenger
    ) {
        this.plugin = plugin;
        this.storage = storage;
        this.messenger = messenger;

        // Config settings
        this.networkLatencyMilliseconds = Math.max(0, plugin.getConfig().getInt("synchronization.network_latency_milliseconds"));
    }

    @Override public @NotNull Collection<Promise<PlayerData>> getAllCached() {
        return loadingCache.asMap().values();
    }

    @Override public @NotNull Promise<PlayerData> load(final @NotNull UUID uuid) {
        return loadingCache.getUnchecked(uuid);
    }

    @Override public @NotNull Promise<PlayerData> save(final @NotNull PlayerData playerData) {
        return !playerData.complete()
            ? Promise.empty()
            : Promise.supplyingAsync(() -> {
                messenger.send(playerData);
                storage.save(playerData);
                return playerData;
            });
    }

    @Override public @NotNull Promise<UUID> unload(final @NotNull UUID uuid) {
        return Promise.supplyingAsync(() -> {
            loadingCache.invalidate(uuid); // our RemovalListener will save it to disk & publish it to the network
            return uuid;
        });
    }

    @Override public void refresh(final @NotNull UUID uuid) {
        loadingCache.refresh(uuid);
    }

    @Override public void close() {
        // We need to save all online players data before shutdown.
        // Doing so we can safely and completely reload the plugin.
        plugin.getPlayerDataManager().getAllCached().forEach(dataPromise -> dataPromise.thenAcceptSync(storage::save));
    }
}
