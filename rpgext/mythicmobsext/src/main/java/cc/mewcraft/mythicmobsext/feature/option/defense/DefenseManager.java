package cc.mewcraft.mythicmobsext.feature.option.defense;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.MobExecutor;

import java.util.Map;
import java.util.WeakHashMap;

@Singleton
public class DefenseManager {

    private static final String CONFIG_DEFENSE_KEY = "Defense";

    private final Map<MythicMob, Double> defenseConfig;

    @Inject
    public DefenseManager() {
        defenseConfig = new WeakHashMap<>();
    }

    public void register() {
        MobExecutor mobManager = MythicBukkit.inst().getMobManager();
        for (MythicMob type : mobManager.getMobTypes()) {
            double defense = type.getConfig().getDouble(CONFIG_DEFENSE_KEY);
            if (defense > 0D) {
                this.setDefense(type, defense);
            }
        }
    }

    public void unregister() {
        defenseConfig.clear();
    }

    /**
     * Sets the defense of specific mob type.
     *
     * @param type    a mob type
     * @param defense the defense to set
     */
    public void setDefense(MythicMob type, double defense) {
        defenseConfig.put(type, defense);
    }

    /**
     * @param type a mob type
     *
     * @return the defense of the mob type, or 0 if not set
     */
    public double getDefense(MythicMob type) {
        return defenseConfig.getOrDefault(type, 0D);
    }

}
