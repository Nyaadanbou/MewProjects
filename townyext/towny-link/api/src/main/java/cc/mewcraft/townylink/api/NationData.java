package cc.mewcraft.townylink.api;

import com.google.common.collect.ImmutableSet;
import me.lucko.helper.serialize.Point;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.UUID;

public record NationData(
    @NonNull String name,
    @NonNull UUID id,
    @NonNull Point point,
    @NonNull ImmutableSet<UUID> residents
) {
    @Override public String toString() {
        return "NationData{" +
               "name='" + name +
               ", id=" + id +
               ", location=" + point +
               '}';
    }
}
