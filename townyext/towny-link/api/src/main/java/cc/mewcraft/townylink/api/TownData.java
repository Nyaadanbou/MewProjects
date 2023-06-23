package cc.mewcraft.townylink.api;

import com.google.common.collect.ImmutableSet;
import me.lucko.helper.serialize.Position;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

public record TownData(
    @NonNull String name,
    @NonNull UUID id,
    @Nullable Position location,
    @NonNull ImmutableSet<UUID> residents
) {
    @Override public String toString() {
        return "TownData{" +
               "name='" + name + '\'' +
               ", id=" + id +
               ", location=" + location +
               '}';
    }
}
