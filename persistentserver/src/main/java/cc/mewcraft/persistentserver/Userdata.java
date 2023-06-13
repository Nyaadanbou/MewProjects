package cc.mewcraft.persistentserver;

import org.checkerframework.checker.nullness.qual.NonNull;

public record Userdata(
    @NonNull String server
) {}
