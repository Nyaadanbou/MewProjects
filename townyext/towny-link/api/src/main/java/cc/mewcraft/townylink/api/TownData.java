package cc.mewcraft.townylink.api;

import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.UUID;

public record TownData(
    @NonNull String name,
    @NonNull UUID id,
    @Nullable Location location,
    @NonNull List<UUID> residents
) {
    @Override public String toString() {
        return "TownData{" +
               "name='" + name + '\'' +
               ", id=" + id +
               ", location=" + location +
               '}';
    }
}
