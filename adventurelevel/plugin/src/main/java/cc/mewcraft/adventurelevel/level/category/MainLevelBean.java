package cc.mewcraft.adventurelevel.level.category;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.ezylang.evalex.Expression;
import com.google.common.collect.RangeMap;
import org.bukkit.entity.ExperienceOrb;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class MainLevelBean extends AbstractLevelBean {

    private final Map<ExperienceOrb.SpawnReason, Double> experienceModifiers;

    public MainLevelBean(
        final AdventureLevelPlugin plugin,
        final int maxLevel,
        final RangeMap<Integer, Expression> levelToExpFormulae,
        final RangeMap<Integer, Expression> expToLevelFormulae,
        final RangeMap<Integer, Expression> nextLevelFormulae,
        final Map<ExperienceOrb.SpawnReason, Double> experienceModifiers
    ) {
        super(plugin, maxLevel, levelToExpFormulae, expToLevelFormulae, nextLevelFormulae);
        this.experienceModifiers = experienceModifiers;
    }

    @Override public void handleEvent(final PlayerPickupExperienceEvent event) {
        ExperienceOrb orb = event.getExperienceOrb();
        double amount = orb.getExperience();
        double modifier = experienceModifiers.get(orb.getSpawnReason());
        int result = BigDecimal
            .valueOf(amount * modifier) // apply global modifiers
            .setScale(0, RoundingMode.HALF_DOWN)
            .intValue();
        this.addExperience(result);
    }
}
