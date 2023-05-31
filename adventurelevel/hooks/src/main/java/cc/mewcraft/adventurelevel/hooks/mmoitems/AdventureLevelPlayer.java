package cc.mewcraft.adventurelevel.hooks.mmoitems;

import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import org.jetbrains.annotations.NotNull;

public class AdventureLevelPlayer extends RPGPlayer {

    private volatile cc.mewcraft.adventurelevel.data.PlayerData adventurePlayerData; // should be set asynchronously at a later point of time

    public AdventureLevelPlayer(final @NotNull PlayerData mmoPlayerData) {
        super(mmoPlayerData);
    }

    @Override public int getLevel() {
        if (isBackedDataAvailable()) {
            return adventurePlayerData.getMainLevel().getLevel();
        }
        return 0;
    }

    @Override public String getClassName() {
        return "";
    }

    @Override public double getMana() {
        return getPlayer().getFoodLevel();
    }

    @Override public double getStamina() {
        return 0; // we don't support stamina yet
    }

    @Override public void setMana(final double value) {
        getPlayer().setFoodLevel((int) value);
    }

    @Override public void setStamina(final double value) {
        // we don't support stamina yet
    }

    public cc.mewcraft.adventurelevel.data.PlayerData getAdventurePlayerData() {
        return adventurePlayerData;
    }

    public void setAdventurePlayerData(final @NotNull cc.mewcraft.adventurelevel.data.PlayerData adventurePlayerData) {
        this.adventurePlayerData = adventurePlayerData;
    }

    private boolean isBackedDataAvailable() {
        return adventurePlayerData != null && adventurePlayerData.complete();
    }

}
