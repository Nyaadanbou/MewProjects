package cc.mewcraft.adventurelevel.data;

import me.lucko.helper.promise.Promise;
import me.lucko.helper.terminable.Terminable;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PlayerDataManager extends Terminable {

    @NotNull Promise<PlayerData> load(@NotNull UUID uuid);

    void unload(@NotNull UUID uuid);

    void save(@NotNull PlayerData playerData);

    default @NotNull Promise<PlayerData> load(@NotNull OfflinePlayer player) {
        return this.load(player.getUniqueId());
    }

    default void unload(@NotNull OfflinePlayer player) {
        this.unload(player.getUniqueId());
    }

    default void save(@NotNull OfflinePlayer player) {
        this.load(player.getUniqueId())
            .thenAcceptAsync(this::save);
    }
}
