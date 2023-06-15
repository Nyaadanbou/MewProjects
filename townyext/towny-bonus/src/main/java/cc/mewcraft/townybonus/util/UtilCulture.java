package cc.mewcraft.townybonus.util;

import cc.mewcraft.townybonus.object.culture.Culture;
import cc.mewcraft.townybonus.object.culture.CultureLevel;
import cc.mewcraft.townybonus.TownyBonus;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.lucko.helper.utils.annotation.NonnullByDefault;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

@NonnullByDefault
public final class UtilCulture {

    /**
     * @param culture a culture
     * @param player  a player
     * @return true if the player belongs to the culture, otherwise false
     */
    public static boolean belongsCulture(Culture culture, Player player) {
        return culture.equals(cultureOfNullable(player));
    }

    /**
     * @param residentUuid a resident UUID
     * @return the culture the resident belongs to
     */
    public static @Nullable Culture cultureOfNullable(UUID residentUuid) {
        final Resident resident = TownyAPI.getInstance().getResident(residentUuid);
        if (resident == null) return null;
        final Nation nation = TownyAPI.getInstance().getResidentNationOrNull(resident);
        if (nation == null) return null;

        final String culture = UtilMetadata.getCulture(nation);
        return TownyBonus.p.cultureManager.getOrNull(culture);
    }

    /**
     * @see #cultureOfNullable(UUID)
     */
    public static @Nullable Culture cultureOfNullable(Player player) {
        return cultureOfNullable(player.getUniqueId());
    }

    /**
     * @param nation a nation
     * @return the culture of the nation
     */
    public static @Nullable Culture cultureOfNullable(Nation nation) {
        final String culture = UtilMetadata.getCulture(nation);
        return TownyBonus.p.cultureManager.getOrNull(culture);
    }

    /**
     * @param town a town
     * @return the culture of the town
     */
    public static @Nullable Culture cultureOfNullable(@NotNull Town town) {
        try {
            final Nation nation = town.getNation();
            return cultureOfNullable(nation);
        } catch (NotRegisteredException ignore) {
        }
        return null;
    }

    /**
     * @see #cultureOfNullable(UUID)
     */
    public static Optional<Culture> cultureOf(UUID residentUuid) {
        return Optional.ofNullable(cultureOfNullable(residentUuid));
    }

    /**
     * @see #cultureOf(UUID)
     */
    public static Optional<Culture> cultureOf(Player player) {
        return cultureOf(player.getUniqueId());
    }

    /**
     * @see #cultureOfNullable(Nation)
     */
    public static Optional<Culture> cultureOf(Nation nation) {
        return Optional.ofNullable(cultureOfNullable(nation));
    }

    /**
     * @see #cultureOfNullable(Town)
     */
    public static Optional<Culture> cultureOf(Town town) {
        return Optional.ofNullable(cultureOfNullable(town));
    }

    /**
     * @param residentUuid a resident UUID
     * @return the culture level that the resident belongs to
     */
    public static @Nullable CultureLevel cultureLevelOfNullable(UUID residentUuid) {
        // 1. find resident by uuid
        // 2. find nation by resident
        // 3. find culture by nation
        // 4. calculate [culture level] by nation
        // we need to refer Culture object

        final int nationLevel = UtilTowny.getNationLevel(residentUuid);
        final Culture culture = cultureOfNullable(residentUuid);
        if (nationLevel != -1 && culture != null) {
            return culture.getCultureLevelOrNull(nationLevel);
        }
        return null;
    }

    /**
     * @see #cultureLevelOfNullable(UUID)
     */
    public static @Nullable CultureLevel cultureLevelOfNullable(Player player) {
        return cultureLevelOfNullable(player.getUniqueId());
    }

    /**
     * @param nation a nation
     * @return the culture level of the nation
     */
    public static @Nullable CultureLevel cultureLevelOfNullable(@NotNull Nation nation) {
        final Culture culture = cultureOfNullable(nation);
        if (culture != null) {
            return culture.getCultureLevelOrNull(nation.getLevel());
        }
        return null;
    }

    /**
     * @param town a town
     * @return the culture level of the town
     */
    public static @Nullable CultureLevel cultureLevelOfNullable(Town town) {
        try {
            final Nation nation = town.getNation();
            return cultureLevelOfNullable(nation);
        } catch (NotRegisteredException ignored) {
        }
        return null;
    }

    /**
     * @see #cultureLevelOfNullable(UUID)
     */
    public static Optional<CultureLevel> cultureLevelOf(UUID residentUuid) {
        return Optional.ofNullable(cultureLevelOfNullable(residentUuid));
    }

    /**
     * @see #cultureLevelOfNullable(Player)
     */
    public static Optional<CultureLevel> cultureLevelOf(Player player) {
        return Optional.ofNullable(cultureLevelOfNullable(player.getUniqueId()));
    }

    /**
     * @see #cultureLevelOfNullable(Nation)
     */
    public static Optional<CultureLevel> cultureLevelOf(Nation nation) {
        return Optional.ofNullable(cultureLevelOfNullable(nation));
    }

    /**
     * @see #cultureLevelOfNullable(Town)
     */
    public static Optional<CultureLevel> cultureLevelOf(Town town) {
        return Optional.ofNullable(cultureLevelOfNullable(town));
    }
}
