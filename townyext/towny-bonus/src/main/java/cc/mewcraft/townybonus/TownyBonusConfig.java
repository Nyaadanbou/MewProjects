package cc.mewcraft.townybonus;

public class TownyBonusConfig {

    private final TownyBonus p;

    public TownyBonusConfig(TownyBonus p) {
        this.p = p;
    }

    public void loadDefaultConfig() {
        p.saveDefaultConfig();
        p.reloadConfig();
    }

    public void reloadConfig() {
        p.reloadConfig();
    }

    public boolean getDebug() {
        return p.getConfig().getBoolean("debug");
    }

    public String getDefaultCulturePage() {
        return p.getConfig().getString("defaultCulturePageURL");
    }

    public String getMituanGroupName() {
        return p.getConfig().getString("mituanBonus.groupName");
    }

    public int getMituanTownUpkeepMax() {
        return p.getConfig().getInt("mituanBonus.townUpkeepMultiplier.max");
    }

    public int getMituanTownUpkeepBase() {
        return p.getConfig().getInt("mituanBonus.townUpkeepMultiplier.base");
    }

    public int getMituanNationUpkeepMax() {
        return p.getConfig().getInt("mituanBonus.nationUpkeepMultiplier.max");
    }

    public int getMituanNationUpkeepBase() {
        return p.getConfig().getInt("mituanBonus.nationUpkeepMultiplier.base");
    }

}

