package cc.mewcraft.mythicmobsext.feature.option.crit;

import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.damage.ProjectileAttackMetadata;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class CriticalUtils {

    /**
     * Checks if the attack in the {@link PlayerAttackEvent} is vanilla critical.
     * <p>
     * For custom bows of MMOItems, this method always return false because custom bows cannot trigger the vanilla
     * critical damage even if the pull force is full ({@link AbstractArrow#isCritical()} is false). Custom bows always
     * deal "static" damage as the player stats. For other cases like melee attacks or vanilla bows, this method returns
     * the status normally.
     *
     * @param event the event
     *
     * @return true if the attack is vanilla critical, otherwise false
     */
    public static boolean isVanillaCriticalHit(@NotNull PlayerAttackEvent event) {
        if (event.toBukkit() instanceof EntityDamageByEntityEvent damageEvent && damageEvent.isCritical()) {
            if (event.getAttack() instanceof ProjectileAttackMetadata meta &&
                meta.getProjectile() instanceof AbstractArrow arrow &&
                MMOItems.plugin.getEntities().isCustomProjectile(arrow)) {

                // It's a critical hit from a custom bow of MMOItems.

                // Set to `false` because custom bows cannot actually deal critical damage.
                // This is somewhat useless because EntityDamageByEntityEvent#isCritical()
                // still returns `true` even if we do the operation below.
                arrow.setCritical(false);
                return false;

            }

            // It's a critical hit from a melee attack or vanilla bow.
            return true;

        }

        // Not a critical hit at all.
        return false;
    }

}
