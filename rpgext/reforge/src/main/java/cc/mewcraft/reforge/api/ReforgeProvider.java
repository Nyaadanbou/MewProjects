package cc.mewcraft.reforge.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class ReforgeProvider {
    private static Reforge instance = null;

    /**
     * Provides static access to the {@link Reforge} API.
     *
     * <p>Ideally, the ServiceManager for the platform should be used to obtain an
     * instance, however, this provider can be used if this is not viable.</p>
     *
     * @return an instance of the Reforge API
     * @throws IllegalStateException if the API is not loaded yet
     */
    public static @NotNull Reforge get() {
        Reforge instance = ReforgeProvider.instance;
        if (instance == null) {
            throw new IllegalStateException("Instance is not loaded yet.");
        }
        return instance;
    }

    @ApiStatus.Internal
    public static void register(Reforge instance) {
        ReforgeProvider.instance = instance;
    }

    @ApiStatus.Internal
    public static void unregister() {
        ReforgeProvider.instance = null;
    }

    @ApiStatus.Internal
    private ReforgeProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }
}
