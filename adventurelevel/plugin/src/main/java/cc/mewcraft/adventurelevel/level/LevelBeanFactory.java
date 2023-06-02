package cc.mewcraft.adventurelevel.level;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import cc.mewcraft.adventurelevel.level.category.*;
import cc.mewcraft.adventurelevel.util.RangeUtils;
import com.ezylang.evalex.Expression;
import com.google.common.base.Preconditions;
import com.google.common.collect.TreeRangeMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ExperienceOrb;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public final class LevelBeanFactory {

    public static @NotNull MainLevelBean createMainLevelBean() {
        AdventureLevelPlugin plugin = AdventureLevelPlugin.getInstance();

        ConfigurationSection config = Objects.requireNonNull(plugin.getConfig().getConfigurationSection("main_level"));

        // Read maximum level
        int maxLevel = config.getInt("maximum_level", 0);

        // Read global xp modifiers
        Map<ExperienceOrb.SpawnReason, Double> globalModifiers = new HashMap<>() {{
            for (final ExperienceOrb.SpawnReason reason : ExperienceOrb.SpawnReason.values()) {
                put(reason, config.getDouble("experience_modifiers." + reason.name().toLowerCase()));
            }
        }};

        // Read level expressions
        TreeRangeMap<Integer, Expression> exp1 = TreeRangeMap.create();
        TreeRangeMap<Integer, Expression> exp2 = TreeRangeMap.create();
        TreeRangeMap<Integer, Expression> exp3 = TreeRangeMap.create();
        ConfigurationSection sec1 = Objects.requireNonNull(config.getConfigurationSection("level_to_exp_formula"));
        ConfigurationSection sec2 = Objects.requireNonNull(config.getConfigurationSection("exp_to_level_formula"));
        ConfigurationSection sec3 = Objects.requireNonNull(config.getConfigurationSection("next_level_exp"));
        Preconditions.checkArgument(sec1.getKeys(false).size() == sec2.getKeys(false).size());
        Preconditions.checkArgument(sec2.getKeys(false).size() == sec3.getKeys(false).size());
        for (final String k : sec1.getKeys(false)) {
            exp1.put(RangeUtils.of(k), new Expression(sec1.getString(k)));
        }
        for (final String k : sec2.getKeys(false)) {
            exp2.put(RangeUtils.of(k), new Expression(sec2.getString(k)));
        }
        for (final String k : sec3.getKeys(false)) {
            exp3.put(RangeUtils.of(k), new Expression(sec3.getString(k)));
        }

        // Create it!
        return new MainLevelBean(plugin, maxLevel, exp1, exp2, exp3, globalModifiers);
    }

    public static @NotNull LevelBean createCateLevelBean(
        @NotNull LevelBean.Category category
    ) {
        AdventureLevelPlugin plugin = AdventureLevelPlugin.getInstance();

        // Load & apply config file

        String configFileName = switch (category) {
            case PLAYER_DEATH -> "player_death.yml";
            case ENTITY_DEATH -> "entity_death.yml";
            case FURNACE -> "furnace.yml";
            case BREED -> "breed.yml";
            case VILLAGER_TRADE -> "villager_trade.yml";
            case FISHING -> "fishing.yml";
            case BLOCK_BREAK -> "block_break.yml";
            case EXP_BOTTLE -> "exp_bottle.yml";
            case GRINDSTONE -> "grindstone.yml";
        };

        File file = plugin.getDataFolder().toPath().resolve("categories").resolve(configFileName).toFile();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        int maxLevel = config.getInt("maximum_level", 0);

        TreeRangeMap<Integer, Expression> exp1 = TreeRangeMap.create();
        TreeRangeMap<Integer, Expression> exp2 = TreeRangeMap.create();
        TreeRangeMap<Integer, Expression> exp3 = TreeRangeMap.create();
        ConfigurationSection sec1 = Objects.requireNonNull(config.getConfigurationSection("level_to_exp_formula"));
        ConfigurationSection sec2 = Objects.requireNonNull(config.getConfigurationSection("exp_to_level_formula"));
        ConfigurationSection sec3 = Objects.requireNonNull(config.getConfigurationSection("next_level_exp"));
        Preconditions.checkArgument(sec1.getKeys(false).size() == sec2.getKeys(false).size());
        Preconditions.checkArgument(sec2.getKeys(false).size() == sec3.getKeys(false).size());
        for (final String k : sec1.getKeys(false)) {
            exp1.put(RangeUtils.of(k), new Expression(sec1.getString(k)));
        }
        for (final String k : sec2.getKeys(false)) {
            exp2.put(RangeUtils.of(k), new Expression(sec2.getString(k)));
        }
        for (final String k : sec3.getKeys(false)) {
            exp3.put(RangeUtils.of(k), new Expression(sec3.getString(k)));
        }

        // Construct the instance
        return switch (category) {
            case PLAYER_DEATH -> new PlayerDeathLevelBean(plugin, maxLevel, exp1, exp2, exp3);
            case ENTITY_DEATH -> new EntityDeathLevelBean(plugin, maxLevel, exp1, exp2, exp3);
            case FURNACE -> new FurnaceLevelBean(plugin, maxLevel, exp1, exp2, exp3);
            case BREED -> new BreedLevelBean(plugin, maxLevel, exp1, exp2, exp3);
            case VILLAGER_TRADE -> new VillagerTradeLevelBean(plugin, maxLevel, exp1, exp2, exp3);
            case FISHING -> new FishingLevelBean(plugin, maxLevel, exp1, exp2, exp3);
            case BLOCK_BREAK -> new BlockBreakLevelBean(plugin, maxLevel, exp1, exp2, exp3);
            case EXP_BOTTLE -> new ExpBottleLevelBean(plugin, maxLevel, exp1, exp2, exp3);
            case GRINDSTONE -> new GrindstoneLevelBean(plugin, maxLevel, exp1, exp2, exp3);
        };
    }

}
