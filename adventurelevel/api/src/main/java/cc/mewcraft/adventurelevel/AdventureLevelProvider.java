package cc.mewcraft.adventurelevel;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public final class AdventureLevelProvider {

    private static AdventureLevel instance = null;

    /**
     * Provides static access to the {@link AdventureLevel} API.
     *
     * <p>Ideally, the ServiceManager for the platform should be used to obtain an
     * instance, however, this provider can be used if this is not viable.</p>
     *
     * @return an instance of the AdventureLevel API
     *
     * @throws IllegalStateException if the API is not loaded yet
     */
    public static @NotNull AdventureLevel get() {
        AdventureLevel instance = AdventureLevelProvider.instance;
        if (instance == null) {
            throw new IllegalStateException("Instance is not loaded yet.");
        }
        return instance;
    }

    @ApiStatus.Internal
    static void register(AdventureLevel instance) {
        AdventureLevelProvider.instance = instance;
    }

    @ApiStatus.Internal
    static void unregister() {
        AdventureLevelProvider.instance = null;
    }

    @ApiStatus.Internal
    private AdventureLevelProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

}
