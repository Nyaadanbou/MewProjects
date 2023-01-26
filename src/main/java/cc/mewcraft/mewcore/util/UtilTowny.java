package cc.mewcraft.mewcore.util;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownBlockType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UtilTowny {

    private static final TownyAPI townyApi = TownyAPI.getInstance();

    public static boolean isMayor(UUID uuid) {
        Resident resident = townyApi.getResident(uuid);
        if (resident == null) return false;
        return resident.isMayor();
    }

    /**
     * Checks if the player is a resident of a given location
     *
     * @param player   Player to check
     * @param location Location
     * @return Is the player a resident of this location?
     */
    public static boolean isResident(Player player, Location location) {
        TownBlock townBlock = TownyAPI.getInstance().getTownBlock(location);
        try {
            if (townBlock != null) {
                return townBlock.getTown().hasResident(player.getName());
            }
            return false;
        } catch (NotRegisteredException ex) {
            return false;
        }
    }

    /**
     * Checks if the player is a resident of given locations
     *
     * @param player    Player to check
     * @param locations Locations
     * @return Is the player a resident of those locations?
     */
    public static boolean isResident(Player player, Location... locations) {
        for (Location location : locations) {
            if (!isResident(player, location)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the location is in wilderness
     *
     * @param location Location
     * @return Is the location in wilderness?
     */
    public static boolean isInWilderness(Location location) {
        return TownyAPI.getInstance().isWilderness(location);
    }

    /**
     * Checks if the locations are in wilderness
     *
     * @param locations Locations
     * @return Are the locations in wilderness?
     */
    public static boolean isInWilderness(Location... locations) {
        for (Location location : locations) {
            if (location != null && !isInWilderness(location)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the location is inside shop plot
     *
     * @param location Location to check
     * @return Is the location inside shop plot?
     */
    public static boolean isInsideShopPlot(Location location) {
        TownBlock townBlock = TownyAPI.getInstance().getTownBlock(location);
        if (townBlock != null) {
            return townBlock.getType() == TownBlockType.COMMERCIAL;
        }

        return false;
    }

    /**
     * Checks if the locations are inside shop plots
     *
     * @param locations Locations to check
     * @return Are the location inside shop plots?
     */
    public static boolean isInsideShopPlot(Location... locations) {
        for (Location location : locations) {
            if (location != null && !isInsideShopPlot(location)) {
                return false;
            }
        }

        return true;
    }

}
