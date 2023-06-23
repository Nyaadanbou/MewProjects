package cc.mewcraft.townylink.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class TownyLinkProvider {
    private static TownyLink instance = null;

    /**
     * Provides static access to the {@link TownyLink} API.
     *
     * <p>Ideally, the ServiceManager for the platform should be used to obtain an
     * instance, however, this provider can be used if this is not viable.</p>
     *
     * @return an instance of the TownyLink API
     * @throws IllegalStateException if the API is not loaded yet
     */
    public static @NotNull TownyLink get() {
        TownyLink instance = TownyLinkProvider.instance;
        if (instance == null) {
            throw new IllegalStateException("Instance is not loaded yet.");
        }
        return instance;
    }

    @ApiStatus.Internal
    public static void register(TownyLink instance) {
        TownyLinkProvider.instance = instance;
    }

    @ApiStatus.Internal
    public static void unregister() {
        TownyLinkProvider.instance = null;
    }

    @ApiStatus.Internal
    private TownyLinkProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }
}
