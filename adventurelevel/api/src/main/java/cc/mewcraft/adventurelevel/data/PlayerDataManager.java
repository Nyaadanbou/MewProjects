package cc.mewcraft.adventurelevel.data;

import me.lucko.helper.promise.Promise;
import me.lucko.helper.terminable.Terminable;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

public interface PlayerDataManager extends Terminable {

    /**
     * Implementation Notes: this should save all cached player data to file when being called.
     */
    @Override void close() throws Exception;

    @NotNull Collection<Promise<PlayerData>> getAllCached();

    @NotNull Promise<PlayerData> load(@NotNull UUID uuid);

    @NotNull Promise<PlayerData> save(@NotNull PlayerData playerData);

    @NotNull Promise<UUID> unload(@NotNull UUID uuid);

    void refresh(@NotNull UUID uuid);

    default @NotNull Promise<PlayerData> load(@NotNull OfflinePlayer player) {
        return this.load(player.getUniqueId());
    }

    default @NotNull Promise<UUID> unload(@NotNull OfflinePlayer player) {
        return this.unload(player.getUniqueId());
    }

    default @NotNull Promise<PlayerData> save(@NotNull OfflinePlayer player) {
        return this.load(player.getUniqueId()).thenComposeAsync(this::save);
    }
}
