package cc.mewcraft.townylink.api;

import com.google.common.collect.ImmutableSet;
import me.lucko.helper.serialize.Position;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.UUID;

public record NationData(
    @NonNull String name,
    @NonNull UUID id,
    @NonNull Position location,
    @NonNull ImmutableSet<UUID> residents
) {
    @Override public String toString() {
        return "NationData{" +
               "name='" + name +
               ", id=" + id +
               ", location=" + location +
               '}';
    }
}
