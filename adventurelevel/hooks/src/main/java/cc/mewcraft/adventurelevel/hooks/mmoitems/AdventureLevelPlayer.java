package cc.mewcraft.adventurelevel.hooks.mmoitems;

import cc.mewcraft.adventurelevel.AdventureLevelProvider;
import cc.mewcraft.adventurelevel.level.category.LevelCategory;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AdventureLevelPlayer extends RPGPlayer {
    private final UUID uuid;

    public AdventureLevelPlayer(final @NotNull PlayerData mmoPlayerData) {
        super(mmoPlayerData);
        this.uuid = mmoPlayerData.getUniqueId();
    }

    @Override public int getLevel() {
        cc.mewcraft.adventurelevel.data.PlayerData adventurePlayerData = AdventureLevelProvider.get().getPlayerDataManager().load(uuid);

        if (adventurePlayerData.complete()) {
            return adventurePlayerData.getLevel(LevelCategory.MAIN).getLevel();
        } else {
            return 0;
        }
    }

    @Override public String getClassName() {
        return "None";
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
