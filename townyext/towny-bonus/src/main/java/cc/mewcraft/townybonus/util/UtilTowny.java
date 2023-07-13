package cc.mewcraft.townybonus.util;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class UtilTowny {

    /**
     * @param uuid a resident UUID
     * @return the level of the town that the resident belongs to. Returns -1 if
     * the resident does not belong to a town
     */
    public static int getTownLevel(@NotNull UUID uuid) {
        final Resident resident = TownyAPI.getInstance().getResident(uuid);
        if (resident != null) {
            final Town townOrNull = resident.getTownOrNull();
            if (townOrNull == null) {
                return -1;
            } else {
                return townOrNull.getLevel();
            }
        }
        return -1;
    }

    /**
     * @param uuid a resident UUID
     * @return the nation level of the nation that the resident belongs to.
     * Returns -1 if the resident does not belong to a nation
     */
    public static int getNationLevel(@NotNull UUID uuid) {
        final Resident resident = TownyAPI.getInstance().getResident(uuid);
        if (resident != null) {
            final Nation nationOrNull = resident.getNationOrNull();
            if (nationOrNull == null) {
                return -1;
            } else {
                return nationOrNull.getLevel();
            }
        }
        return -1;
    }

    /**
     * @see #getTownLevel(Player)
     */
    public static int getTownLevel(@NotNull Player p) {
        return getTownLevel(p.getUniqueId());
    }

    /**
     * @see #getNationLevel(Player)
     */
    public static int getNationLevel(@NotNull Player p) {
        return getNationLevel(p.getUniqueId());
    }
}
