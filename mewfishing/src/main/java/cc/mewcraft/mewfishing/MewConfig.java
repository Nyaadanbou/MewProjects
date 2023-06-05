package cc.mewcraft.mewfishing;

import java.util.List;

public class MewConfig {

    private final MewFishing plugin;

    public MewConfig(MewFishing plugin) {
        this.plugin = plugin;
    }

    public void loadDefaultConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
    }

    public int networkLatency() {
        return plugin.getConfig().getInt("network_latency");
    }

    public boolean fishPowerEnabled() {
        return plugin.getConfig().getBoolean("fishing_power.enabled");
    }

    public void setFishPowerEnabled(boolean enabled) {
        plugin.getConfig().set("fishing_power.enabled", enabled);
    }

    public int powerTimeout() {
        return plugin.getConfig().getInt("fishing_power.timeout");
    }

    /*public long powerStacks() {
        return plugin.getConfig().getLong("fishing_power.stacks");
    }*/

    public String currencyName() {
        return plugin.getConfig().getString("fishing_power.currency_name");
    }

    public int progressbarWidth() {
        return plugin.getConfig().getInt("fishing_power.progressbar_width");
    }

    public int progressbarStayTime() {
        return plugin.getConfig().getInt("fishing_power.progressbar_stay_time");
    }

    public List<String> freeItems() {
        return plugin.getConfig().getStringList("fishing_power.free_items");
    }

    public boolean autoFishEnabled() {
        return plugin.getConfig().getBoolean("auto_fishing.enabled");
    }

    public int ticksAfterBitten() {
        return plugin.getConfig().getInt("auto_fishing.ticks_after_bitten");
    }

    public int ticksAfterCaught() {
        return plugin.getConfig().getInt("auto_fishing.ticks_after_caught");
    }

    public double customLootChance() {
        return plugin.getConfig().getDouble("fishing_loots.chance");
    }

}

