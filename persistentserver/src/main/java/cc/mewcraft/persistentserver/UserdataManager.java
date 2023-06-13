package cc.mewcraft.persistentserver;

import com.google.common.cache.*;
import com.google.gson.Gson;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class UserdataManager {
    /* Constants */
    private static final Path LOCATION_PATH = Path.of("locations");
    private static final long CACHE_TIMEOUT_SEC = 300L;
    private static final long MAXIMUM_SIZE = 200L;

    /* Caches */
    private final LoadingCache<UUID, Optional<RegisteredServer>> lastServerCache;
    private final LoadingCache<UUID, Path> userdataPathCache;

    private final Path dataDir;
    private final Gson gson;

    private class LastServerCacheLoader extends CacheLoader<UUID, Optional<RegisteredServer>> {
        @Override public @NonNull Optional<RegisteredServer> load(final @NonNull UUID uuid) throws Exception {
            Path userdataPath = getUserdataPath(uuid);

            if (userdataPath.toFile().createNewFile()) {
                // file does not exist - return an empty optional
                return Optional.empty();
            }

            // the file exists - read it and return server name
            try (BufferedReader reader = new BufferedReader(new FileReader(userdataPath.toFile()))) {
                Userdata userdata = gson.fromJson(reader, Userdata.class);
                return PersistentServer.SERVER.getServer(userdata.server());
            } catch (Exception e) {
                PersistentServer.LOGGER.error(e.getMessage(), e.getCause());
                return Optional.empty();
            }
        }
    }

    private class LastServerRemovalListener implements RemovalListener<UUID, Optional<RegisteredServer>> {
        @Override public void onRemoval(final RemovalNotification<UUID, Optional<RegisteredServer>> notification) {
            // when the entry is removed from cache,
            // write the information to file

            Objects.requireNonNull(notification.getValue()).ifPresent(server -> {
                UUID uuid = Objects.requireNonNull(notification.getKey());
                Path userdataPath = getUserdataPath(uuid);
                String json = gson.toJson(new Userdata(server.getServerInfo().getName()), Userdata.class);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(userdataPath.toFile()))) {
                    writer.write(json);
                } catch (Exception e) {
                    PersistentServer.LOGGER.error(e.getMessage(), e.getCause());
                }
            });
        }
    }

    public UserdataManager(final Path dataDir) {
        this.dataDir = dataDir;

        Path locationPath = dataDir.resolve(LOCATION_PATH);
        if (locationPath.toFile().mkdirs()) {
            PersistentServer.LOGGER.info("{} does not exist - creating one", locationPath);
        }

        this.lastServerCache = CacheBuilder.newBuilder()
            .expireAfterAccess(Duration.of(CACHE_TIMEOUT_SEC, ChronoUnit.SECONDS))
            .removalListener(new LastServerRemovalListener())
            .maximumSize(MAXIMUM_SIZE)
            .build(new LastServerCacheLoader());
        this.userdataPathCache = CacheBuilder.newBuilder()
            .expireAfterAccess(Duration.of(CACHE_TIMEOUT_SEC, ChronoUnit.SECONDS))
            .maximumSize(MAXIMUM_SIZE)
            .build(CacheLoader.from(uuid -> locationPath.resolve(uuid.toString() + ".json")));
        this.gson = new Gson();
    }

    /**
     * Clean up all data in memory, saving to files.
     */
    public void cleanup() {
        lastServerCache.invalidateAll();
    }

    /**
     * @param uuid the uuid of the player
     *
     * @return the last server that the player just quit from
     */
    public @NonNull Optional<RegisteredServer> getLastServer(@NonNull UUID uuid) {
        return lastServerCache.getUnchecked(uuid);
    }

    /**
     * Saves the server that the player just quit from.
     *
     * @param uuid   the uuid of the player who just disconnected from the proxy
     * @param server the server that the player just quit from
     */
    public void saveLastServer(@NonNull UUID uuid, @NonNull RegisteredServer server) {
        lastServerCache.put(uuid, Optional.of(server));
    }

    private Path getUserdataPath(UUID uuid) {
        return userdataPathCache.getUnchecked(uuid);
    }
}
