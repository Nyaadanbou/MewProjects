package cc.mewcraft.mewfishing;

import java.util.List;

public class MewConfig {

    private final MewFishing p;

    public MewConfig(MewFishing p) {
        this.p = p;
    }

    public void loadDefaultConfig() {
        p.saveDefaultConfig();
        p.reloadConfig();
    }

    public boolean debug() {
        return p.getConfig().getBoolean("debug");
    }

    public boolean fishingPowerEnabled() {
        return p.getConfig().getBoolean("fishingPower.enabled");
    }

    public void setFishingPowerEnabled(boolean enabled) {
        p.getConfig().set("fishingPower.enabled", enabled);
    }

    public int baseTimeout() {
        return p.getConfig().getInt("fishingPower.baseTimeout");
    }

    public String currencyName() {
        return p.getConfig().getString("fishingPower.currencyName");
    }

    public int progressbarWidth() {
        return p.getConfig().getInt("fishingPower.progressbarWidth");
    }

    public int progressbarStayTime() {
        return p.getConfig().getInt("fishingPower.progressbarStayTime");
    }

    public List<String> freeItems() {
        return p.getConfig().getStringList("fishingPower.freeItems");
    }

    public boolean autoFishingEnabled() {
        return p.getConfig().getBoolean("autoFishing.enabled");
    }

    public int ticksAfterBitten() {
        return p.getConfig().getInt("autoFishing.ticksAfterBitten");
    }

    public int ticksAfterCaught() {
        return p.getConfig().getInt("autoFishing.ticksAfterCaught");
    }

    public double customLootChance() {
        return p.getConfig().getDouble("fishingLoots.chance");
    }
}

