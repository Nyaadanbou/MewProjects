package co.mcsky.mmoext.damage.defense;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.WeakHashMap;

public class DefenseManager {

    private static final String CONFIG_DEFENSE_KEY = "Defense";
    private static DefenseManager INSTANCE;

    public static @NotNull DefenseManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DefenseManager();
        }
        return INSTANCE;
    }

    private final Map<MythicMob, Double> defenseConfig;

    public DefenseManager() {
        this.defenseConfig = new WeakHashMap<>();
    }

    public void unregisterAll() {
        this.defenseConfig.clear();
    }

    public void registerAll() {
        MobExecutor mobManager = MythicBukkit.inst().getMobManager();
        for (MythicMob type : mobManager.getMobTypes()) {
            double defense = type.getConfig().getDouble(CONFIG_DEFENSE_KEY);
            if (defense > 0D) this.setDefense(type, defense);
        }
    }

    public void setDefense(MythicMob type, double defense) {
        this.defenseConfig.put(type, defense);
    }

    /**
     * @param type a mob type
     *
     * @return the defense of the mob type, or 0 if not set
     */
    public double getDefense(MythicMob type) {
        Double defense = this.defenseConfig.get(type);
        return defense != null ? defense : 0D;
    }

}
