package cc.mewcraft.adventurelevel.data;

import me.lucko.helper.promise.Promise;
import me.lucko.helper.terminable.Terminable;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public interface PlayerDataManager extends Terminable {

    @NotNull Promise<PlayerData> load(@NotNull UUID uuid);

    default @NotNull Promise<PlayerData> load(@NotNull OfflinePlayer player) {
        return load(player.getUniqueId());
    }

    @NotNull Promise<PlayerData> save(@NotNull PlayerData playerData);

    default @NotNull Promise<PlayerData> save(@NotNull OfflinePlayer player) {
        return load(player.getUniqueId()).thenComposeAsync(this::save);
    }

    @NotNull Promise<UUID> unload(@NotNull UUID uuid);

    default @NotNull Promise<UUID> unload(@NotNull OfflinePlayer player) {
        return unload(player.getUniqueId());
    }

    void refresh(@NotNull UUID uuid);

    @NotNull Map<UUID, Promise<PlayerData>> asMap();

    /**
     * Implementation Requirement: This should save all cached player data to file when being called.
     */
    @Override void close() throws Exception;
}
