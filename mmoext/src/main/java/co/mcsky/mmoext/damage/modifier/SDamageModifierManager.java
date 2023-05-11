package co.mcsky.mmoext.damage.modifier;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.MobExecutor;
import io.lumine.mythic.lib.damage.DamageType;

import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * This class manages the <b>static</b> modifiers applied to MM mobs.
 * <p>
 * Static modifiers are usually applied through the <code>Options</code> config of MM mobs.
 */
public class SDamageModifierManager {

    private static SDamageModifierManager INSTANCE;
    private final Map<MythicMob, DamageModifiers> modifiers = new WeakHashMap<>();

    public static SDamageModifierManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SDamageModifierManager();
        }
        return INSTANCE;
    }

    public void unregisterAll() {
        modifiers.clear();
    }

    public void registerAll() {
        MobExecutor mobManager = MythicBukkit.inst().getMobManager();
        for (MythicMob mob : mobManager.getMobTypes()) {
            for (String reduction : mob.getConfig().getStringList("MMODamageModifiers")) {
                String[] reductions = reduction.split(" ");
                DamageType damageType = DamageType.valueOf(reductions[0].toUpperCase(Locale.ROOT));
                double multiplier = Double.parseDouble(reductions[1]);
                modifiers.computeIfAbsent(
                    mob, key -> new DamageModifiers()
                ).addModifier(damageType, multiplier);
            }
        }
    }

    public DamageModifiers getModifiers(MythicMob type) {
        return modifiers.get(type);
    }

}