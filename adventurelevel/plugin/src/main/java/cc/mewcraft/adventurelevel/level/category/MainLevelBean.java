package cc.mewcraft.adventurelevel.level.category;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.ezylang.evalex.Expression;
import org.bukkit.entity.ExperienceOrb;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class MainLevelBean extends AbstractLevelBean {

    private final Map<ExperienceOrb.SpawnReason, Double> globalModifiers;

    public MainLevelBean(
        final AdventureLevelPlugin plugin,
        final int maxLevel,
        final Expression levelToExpFormula,
        final Expression expToLevelFormula,
        final Expression nextLevelFormula,
        final Map<ExperienceOrb.SpawnReason, Double> globalModifiers
    ) {
        super(plugin, maxLevel, levelToExpFormula, expToLevelFormula, nextLevelFormula);
        this.globalModifiers = globalModifiers;
    }

    @Override public void handleEvent(final PlayerPickupExperienceEvent event) {
        ExperienceOrb orb = event.getExperienceOrb();
        double amount = orb.getExperience();
        double modifier = globalModifiers.get(orb.getSpawnReason());
        int result = BigDecimal
            .valueOf(amount * modifier) // apply global modifiers
            .setScale(0, RoundingMode.HALF_DOWN)
            .intValue();
        this.addExperience(result);
    }
}
