package cc.mewcraft.mythicmobsext.feature.option.crit;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.MobExecutor;
import io.lumine.mythic.lib.UtilityMethods;

import java.util.Map;
import java.util.WeakHashMap;

@Singleton
public class CriticalHitManager {

    private final Map<MythicMob, CriticalHitModifiers> modifiers = new WeakHashMap<>();

    @Inject
    public CriticalHitManager() {}

    public void register() {
        MobExecutor mobManager = MythicBukkit.inst().getMobManager();
        for (MythicMob mob : mobManager.getMobTypes()) {
            for (String reduction : mob.getConfig().getStringList("CriticalHitModifiers")) {
                String[] reductions = reduction.split(" ");
                CriticalHitType criticalHitType = CriticalHitType.valueOf(UtilityMethods.enumName(reductions[0]));
                double coefficient = Double.parseDouble(reductions[1]);
                modifiers.computeIfAbsent(
                    mob, key -> new CriticalHitModifiers()
                ).addType(criticalHitType, coefficient);
            }
        }
    }

    public void unregister() {
        modifiers.clear();
    }

    public CriticalHitModifiers getModifiers(MythicMob type) {
        return modifiers.get(type);
    }

}
