package cc.mewcraft.nms;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class MewNMSProvider {
    static MewNMS instance = null;

    public static @NotNull MewNMS get() {
        MewNMS instance = MewNMSProvider.instance;
        if (instance == null) {
            throw new IllegalStateException("Instance is not loaded yet");
        }
        return instance;
    }

    @ApiStatus.Internal
    static void register(@NotNull MewNMS instance) {
        MewNMSProvider.instance = instance;
    }

    @ApiStatus.Internal
    static void unregister() {
        MewNMSProvider.instance = null;
    }

    @ApiStatus.Internal
    private MewNMSProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }
}
