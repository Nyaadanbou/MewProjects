package cc.mewcraft.townylink.sync.local;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.UUID;

public record GovernmentObject(
    @NonNull UUID uuid,
    @NonNull String name
) {
}
