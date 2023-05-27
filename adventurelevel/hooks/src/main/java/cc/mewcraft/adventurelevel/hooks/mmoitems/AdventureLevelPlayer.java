package cc.mewcraft.adventurelevel.hooks.mmoitems;

import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import org.jetbrains.annotations.NotNull;

public class AdventureLevelPlayer extends RPGPlayer {

    private final cc.mewcraft.adventurelevel.data.PlayerData adventurePlayerData;

    public AdventureLevelPlayer(@NotNull final PlayerData mmoPlayerData, @NotNull final cc.mewcraft.adventurelevel.data.PlayerData adventurePlayerData) {
        super(mmoPlayerData);
        this.adventurePlayerData = adventurePlayerData;
    }

    @Override public int getLevel() {
        return adventurePlayerData.getMainLevel().getLevel();
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

}
