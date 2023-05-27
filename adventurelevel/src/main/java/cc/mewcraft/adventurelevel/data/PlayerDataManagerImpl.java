package cc.mewcraft.adventurelevel.data;

import cc.mewcraft.adventurelevel.AdventureLevel;
import cc.mewcraft.adventurelevel.file.DataStorage;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import me.lucko.helper.promise.Promise;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class PlayerDataManagerImpl implements PlayerDataManager {
    private final AdventureLevel plugin;
    private final DataStorage storage;
    private final LoadingCache<UUID, Promise<PlayerData>> loadingCache = CacheBuilder.newBuilder()
        .expireAfterAccess(Duration.of(5, ChronoUnit.MINUTES))
        .build(new CacheLoader<>() {
            @Override public @NotNull Promise<PlayerData> load(final @NotNull UUID key) {
                return storage.load(key);
            }
        });

    @Inject
    public PlayerDataManagerImpl(
        final AdventureLevel plugin,
        final DataStorage storage
    ) {
        this.plugin = plugin;
        this.storage = storage;
    }

    @Override public @NotNull Promise<PlayerData> load(@NotNull final UUID uuid) {
        return loadingCache.getUnchecked(uuid);
    }

    @Override public void unload(@NotNull final UUID uuid) {
        loadingCache.invalidate(uuid);
    }

    @Override public void save(@NotNull final PlayerData playerData) {
        storage.save(playerData);
    }

    @Override public void close() {
        loadingCache.invalidateAll();
    }
}
