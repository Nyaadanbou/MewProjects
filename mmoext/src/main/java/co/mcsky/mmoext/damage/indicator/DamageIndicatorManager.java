package co.mcsky.mmoext.damage.indicator;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.MobExecutor;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.damage.DamageType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * The entry of DamageAnalysis.
 */
public class DamageIndicatorManager {

    private static DamageIndicatorManager INSTANCE;

    public static @NotNull DamageIndicatorManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DamageIndicatorManager();
        }
        return INSTANCE;
    }

    private final Map<MythicMob, DamageIndicatorParams> tracked;

    public DamageIndicatorManager() {
        tracked = new WeakHashMap<>();
    }

    public void unregisterAll() {
        tracked.clear();
    }

    public void registerAll() {
        MobExecutor mobManager = MythicBukkit.inst().getMobManager();
        for (MythicMob mob : mobManager.getMobTypes()) {
            String configKey = "MMODamageVerbose";
            if (mob.getConfig().isList(configKey)) {
                DamageType[] damageTypes = mob.getConfig()
                        .getStringList(configKey)
                        .stream()
                        .map(type -> DamageType.valueOf(UtilityMethods.enumName(type)))
                        .toArray(DamageType[]::new);
                trackTarget(mob, new DamageIndicatorParams(damageTypes));
            }
        }
    }

    /**
     * Marks specific MM mob so that whenever a player attacks the MM mob, it shows damage information to the player.
     *
     * @param mob    the MM mob
     * @param params the params of how the damage is displayed
     */
    public void trackTarget(@NotNull MythicMob mob, @NotNull DamageIndicatorParams params) {
        tracked.put(mob, params);
    }

    /**
     * Checks whether specific MM mob is being tracked.
     *
     * @param mob the MM mob
     * @return true if the mob is being tracked
     */
    public boolean isTracked(MythicMob mob) {
        return tracked.containsKey(mob);
    }

    /**
     * Gets the params of how the damage is displayed.
     *
     * @param mob the MM mob
     * @return the params of how the damage is displayed
     */
    public @Nullable DamageIndicatorParams getParams(MythicMob mob) {
        return tracked.get(mob);
    }

}
