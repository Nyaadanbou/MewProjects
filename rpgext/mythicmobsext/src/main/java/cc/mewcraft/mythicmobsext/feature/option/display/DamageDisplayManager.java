package cc.mewcraft.mythicmobsext.feature.option.display;

import com.google.inject.Inject;
import com.google.inject.Singleton;
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
@Singleton
public class DamageDisplayManager {

    private final Map<MythicMob, DamageDisplayParams> tracked;

    @Inject
    public DamageDisplayManager() {
        tracked = new WeakHashMap<>();
    }

    public void register() {
        MobExecutor mobManager = MythicBukkit.inst().getMobManager();
        for (MythicMob mob : mobManager.getMobTypes()) {
            String configKey = "MMODamageVerbose";
            if (mob.getConfig().isList(configKey)) {
                DamageType[] damageTypes = mob.getConfig()
                    .getStringList(configKey)
                    .stream()
                    .map(type -> DamageType.valueOf(UtilityMethods.enumName(type)))
                    .toArray(DamageType[]::new);
                trackTarget(mob, new DamageDisplayParams(damageTypes));
            }
        }
    }

    public void unregister() {
        tracked.clear();
    }

    /**
     * Marks specific MM mob so that whenever a player attacks the MM mob, it shows damage information to the player.
     *
     * @param mob    the MM mob
     * @param params the params of how the damage is displayed
     */
    public void trackTarget(@NotNull MythicMob mob, @NotNull DamageDisplayParams params) {
        tracked.put(mob, params);
    }

    /**
     * Checks whether specific MM mob is being tracked.
     *
     * @param mob the MM mob
     *
     * @return true if the mob is being tracked
     */
    public boolean isTracked(MythicMob mob) {
        return tracked.containsKey(mob);
    }

    /**
     * Gets the params of how the damage is displayed.
     *
     * @param mob the MM mob
     *
     * @return the params of how the damage is displayed
     */
    public @Nullable DamageDisplayParams getParameters(MythicMob mob) {
        return tracked.get(mob);
    }

}
