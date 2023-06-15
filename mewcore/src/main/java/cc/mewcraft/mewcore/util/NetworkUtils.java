package cc.mewcraft.mewcore.util;

import cc.mewcraft.mewcore.network.NetworkConstants;
import cc.mewcraft.mewcore.network.ServerInfo;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class NetworkUtils {
    /**
     * Gets the origin server of specific player.
     *
     * @param player the player to get the origin server from
     *
     * @return the origin server of the player, or null if there is none
     */
    public static @Nullable String getOriginServer(@NonNull Player player) {
        return LuckPermsProvider.get()
            .getPlayerAdapter(Player.class)
            .getMetaData(player)
            .getMetaValue(NetworkConstants.SERVER_ORIGIN_ID_KEY);
    }

    /**
     * Checks if specific player has an origin server.
     *
     * @param player the player to test
     *
     * @return true if the player has an origin server; false otherwise
     */
    public static boolean hasOriginServer(@NonNull Player player) {
        return getOriginServer(player) != null;
    }

    /**
     * Checks if specific player is at his origin server.
     *
     * @param player the player to test
     *
     * @return true if the player is at his origin server; false otherwise
     */
    public static boolean atOriginServer(@NonNull Player player) {
        @Nullable String origin = getOriginServer(player);
        @Nullable String server = ServerInfo.SERVER_ID.get().orElse(null);
        return origin != null && origin.equals(server);
    }

    private NetworkUtils() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}
