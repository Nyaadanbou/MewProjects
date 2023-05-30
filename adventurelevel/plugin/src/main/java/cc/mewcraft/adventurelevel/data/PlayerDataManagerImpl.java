package cc.mewcraft.adventurelevel.data;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import cc.mewcraft.adventurelevel.file.DataStorage;
import cc.mewcraft.adventurelevel.message.DataSyncMessenger;
import com.google.common.cache.*;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import me.lucko.helper.promise.Promise;
import me.lucko.helper.utils.Players;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class PlayerDataManagerImpl implements PlayerDataManager {
    private final AdventureLevelPlugin plugin;
    private final DataStorage storage;
    private final DataSyncMessenger messenger;
    private final LoadingCache<UUID, Promise<PlayerData>> loadingCache = CacheBuilder.newBuilder()
        .expireAfterAccess(Duration.of(5, ChronoUnit.MINUTES))
        .removalListener(new PlayerDataRemovalListener())
        .build(new PlayerDataLoader());

    /**
     * Methods define how the data is loaded.
     */
    private class PlayerDataLoader extends CacheLoader<UUID, Promise<PlayerData>> {
        @Override public @NotNull Promise<PlayerData> load(
            final @NotNull UUID key
        ) throws Exception {
            return Promise.supplyingAsync(() -> {
                PlayerData playerData = storage.load(key);

                if (playerData.equals(PlayerData.DUMMY)) {
                    return storage.create(key); // Not existing in datasource - so create one
                }

                return playerData; // Already existing in datasource - just return it
            });
        }

        @Override public @NotNull ListenableFuture<Promise<PlayerData>> reload(
            final @NotNull UUID key,
            final @NotNull Promise<PlayerData> oldValue
        ) throws Exception {
            Promise<PlayerData> playerDataPromise = oldValue
                .thenApplyAsync(oldPlayerData -> {
                    PlayerData newPlayerData = storage.load(key);

                    // We need to keep the old reference but update the internal data.
                    // This ensures that there is only one instance actually being used.
                    oldPlayerData.updateWith(newPlayerData);

                    return oldPlayerData;
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
                Player player = Bukkit.getPlayer(playerData.getUuid());
                if (player != null && player.isOnline()) {
                    messenger.sendData(playerData); // save to messenger
                    storage.save(playerData); // save to disk
                    plugin.getSLF4JLogger().info("Unloaded userdata from cache: {} ({})", player.getName(), player.getUniqueId());
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
    }

    @Override public @NotNull Collection<Promise<PlayerData>> getAllCached() {
        return loadingCache.asMap().values();
    }

    @Override public @NotNull Promise<PlayerData> load(final @NotNull UUID uuid) {
        // We always first try to get data from loading cache
        Promise<PlayerData> cached = loadingCache.getIfPresent(uuid);
        if (cached != null) {
            return cached;
        }

        // If the loading cache does not have the data,
        // then we try to get it from the messenger.
        // We also put the data in loading cache.
        PlayerData temp = messenger.getData(uuid);
        if (temp != null) {
            plugin.getSLF4JLogger().info("Loaded userdata from message store: {} ({})", Players.getOffline(temp.getUuid()).map(OfflinePlayer::getName).orElse("Null"), temp.getUuid());
            Promise<PlayerData> completed = Promise.completed(temp);
            loadingCache.put(uuid, completed);
            return completed;
        }

        // If the messenger does not have the data,
        // then we load the data and return it
        return loadingCache.getUnchecked(uuid);
    }

    @Override public @NotNull Promise<PlayerData> save(final @NotNull PlayerData playerData) {
        return Promise.supplyingAsync(() -> {
            messenger.sendData(playerData); // save to messenger
            storage.save(playerData); // save to disk
            return playerData;
        });
    }

    @Override public @NotNull Promise<UUID> unload(final @NotNull UUID uuid) {
        return Promise.supplyingAsync(() -> {
            loadingCache.invalidate(uuid); // our RemovalListener will save it to disk
            return uuid;
        });
    }

    @Override public void refresh(final @NotNull UUID uuid) {
        loadingCache.refresh(uuid);
    }

    @Override public void close() {
        // We need to save all online players data before shutdown.
        // Doing so we can safely and completely reload the plugin.
        plugin.getPlayerDataManager().getAllCached().forEach(playerDataPromise -> playerDataPromise.thenAcceptSync(storage::save));
    }
}
