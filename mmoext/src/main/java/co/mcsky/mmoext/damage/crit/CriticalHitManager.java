package co.mcsky.mmoext.damage.crit;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.MobExecutor;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.damage.ProjectileAttackMetadata;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Map;
import java.util.WeakHashMap;

public class CriticalHitManager {

    private static CriticalHitManager INSTANCE;
    private final Map<MythicMob, CriticalHitModifiers> modifiers = new WeakHashMap<>();

    public static CriticalHitManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CriticalHitManager();
        }
        return INSTANCE;
    }

    /**
     * Checks if the attack in the {@link io.lumine.mythic.lib.api.event.PlayerAttackEvent} is vanilla critical.
     * <p>
     * For custom bows of MMOItems, this method always return false because custom bows cannot trigger the vanilla
     * critical damage even if the pull force is full ({@link org.bukkit.entity.AbstractArrow#isCritical()} is false).
     * Custom bows always deal "static" damage as the player stats. For other cases like melee attacks or vanilla bows,
     * this method returns the status normally.
     *
     * @param event the event
     * @return true if the attack is vanilla critical, otherwise false
     */
    public static boolean isVanillaCriticalHit(PlayerAttackEvent event) {
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

    public void unregisterAll() {
        modifiers.clear();
    }

    public void registerAll() {
        MobExecutor mobManager = MythicBukkit.inst().getMobManager();
        for (MythicMob mob : mobManager.getMobTypes()) {
            for (String reduction : mob.getConfig().getStringList("CriticalHitModifiers")) {
                String[] reductions = reduction.split(" ");
                CriticalHitType criticalHitType = CriticalHitType.valueOf(UtilityMethods.enumName(reductions[0]));
                double coefficient = Double.parseDouble(reductions[1]);
                modifiers
                        .computeIfAbsent(mob, key -> new CriticalHitModifiers())
                        .addType(criticalHitType, coefficient);
            }
        }
    }

    public CriticalHitModifiers getModifiers(MythicMob type) {
        return modifiers.get(type);
    }

}
