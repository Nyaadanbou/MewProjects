package cc.mewcraft.adventurelevel.level;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import cc.mewcraft.adventurelevel.level.category.*;
import cc.mewcraft.adventurelevel.util.RangeUtils;
import com.ezylang.evalex.Expression;
import com.google.common.base.Preconditions;
import com.google.common.collect.TreeRangeMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ExperienceOrb;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
class LevelBeanBuilder {
    private final AdventureLevelPlugin plugin;
    private final ConfigurationSection config;

    private final int maximumLevel;
    /**
     * Expression: Level to Experience
     */
    private final TreeRangeMap<Integer, Expression> exp1;
    /**
     * Expression: Experience to Level
     */
    private final TreeRangeMap<Integer, Expression> exp2;
    /**
     * Expression: Next Level Experience
     */
    private final TreeRangeMap<Integer, Expression> exp3;

    public static @NotNull LevelBeanBuilder builder(@NotNull AdventureLevelPlugin plugin, @NotNull ConfigurationSection config) {
        Preconditions.checkNotNull(plugin, "plugin");
        Preconditions.checkNotNull(config, "config");

        return new LevelBeanBuilder(plugin, config);
    }

    private LevelBeanBuilder(AdventureLevelPlugin plugin, ConfigurationSection config) {
        this.plugin = plugin;
        this.config = config;

        // Read maximum level
        maximumLevel = config.getInt("maximum_level", 0);

        // Read level expressions
        ConfigurationSection sec1 = Objects.requireNonNull(config.getConfigurationSection("level_to_exp_formula")); // Get sections
        ConfigurationSection sec2 = Objects.requireNonNull(config.getConfigurationSection("exp_to_level_formula"));
        ConfigurationSection sec3 = Objects.requireNonNull(config.getConfigurationSection("next_level_exp"));
        Preconditions.checkArgument(sec1.getKeys(false).size() == sec2.getKeys(false).size()); // Validate # of ranges
        Preconditions.checkArgument(sec2.getKeys(false).size() == sec3.getKeys(false).size());
        exp1 = TreeRangeMap.create(); // Initialize RangeMap's
        exp2 = TreeRangeMap.create();
        exp3 = TreeRangeMap.create();
        fillRangeMap(exp1, sec1); // Fill up RangeMap's
        fillRangeMap(exp2, sec2);
        fillRangeMap(exp3, sec3);
    }

    private void fillRangeMap(TreeRangeMap<Integer, Expression> map, ConfigurationSection section) {
        for (String k : section.getKeys(false)) {
            map.put(RangeUtils.of(k), new Expression(section.getString(k)));
        }
    }

    public @NotNull LevelBean build(@NotNull LevelCategory category) {
        return switch (category) {
            case MAIN -> {
                Map<ExperienceOrb.SpawnReason, Double> experienceModifiers = new HashMap<>() {{
                    for (final ExperienceOrb.SpawnReason reason : ExperienceOrb.SpawnReason.values()) {
                        put(reason, config.getDouble("experience_modifiers." + reason.name().toLowerCase()));
                    }
                }};
                yield new MainLevelBean(plugin, maximumLevel, exp1, exp2, exp3, experienceModifiers);
            }
            case PLAYER_DEATH -> new PlayerDeathLevelBean(plugin, maximumLevel, exp1, exp2, exp3);
            case ENTITY_DEATH -> new EntityDeathLevelBean(plugin, maximumLevel, exp1, exp2, exp3);
            case FURNACE -> new FurnaceLevelBean(plugin, maximumLevel, exp1, exp2, exp3);
            case BREED -> new BreedLevelBean(plugin, maximumLevel, exp1, exp2, exp3);
            case VILLAGER_TRADE -> new VillagerTradeLevelBean(plugin, maximumLevel, exp1, exp2, exp3);
            case FISHING -> new FishingLevelBean(plugin, maximumLevel, exp1, exp2, exp3);
            case BLOCK_BREAK -> new BlockBreakLevelBean(plugin, maximumLevel, exp1, exp2, exp3);
            case EXP_BOTTLE -> new ExpBottleLevelBean(plugin, maximumLevel, exp1, exp2, exp3);
            case GRINDSTONE -> new GrindstoneLevelBean(plugin, maximumLevel, exp1, exp2, exp3);
        };
    }
}
