package cc.mewcraft.adventurelevel.level.category;

import cc.mewcraft.adventurelevel.AdventureLevel;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.ezylang.evalex.Expression;
import org.bukkit.entity.ExperienceOrb;

public class MainLevelBean extends AbstractLevelBean {
    public MainLevelBean(
        final AdventureLevel plugin,
        final int maxLevel,
        final Expression levelToExpFormula,
        final Expression expToLevelFormula,
        final Expression nextLevelFormula
    ) {
        super(plugin, maxLevel, levelToExpFormula, expToLevelFormula, nextLevelFormula);
    }

    @Override public void handleEvent(final PlayerPickupExperienceEvent event) {
        ExperienceOrb orb = event.getExperienceOrb();
        if (orb.getSpawnReason() == ExperienceOrb.SpawnReason.CUSTOM || orb.getSpawnReason() == ExperienceOrb.SpawnReason.UNKNOWN) {
            return; // don't handle these spawn reasons
        }

        this.addExperience(orb.getExperience());
    }
}
