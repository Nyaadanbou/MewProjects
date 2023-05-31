package cc.mewcraft.adventurelevel.level;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import cc.mewcraft.adventurelevel.level.category.*;
import com.ezylang.evalex.Expression;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ExperienceOrb;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class LevelBeanFactory {

    public static @NotNull MainLevelBean createMainLevelBean() {
        AdventureLevelPlugin plugin = AdventureLevelPlugin.getInstance();

        ConfigurationSection config = Objects.requireNonNull(plugin.getConfig().getConfigurationSection("main_level"));
        Map<ExperienceOrb.SpawnReason, Double> globalModifiers = new HashMap<>() {{
            for (final ExperienceOrb.SpawnReason reason : ExperienceOrb.SpawnReason.values())
                put(reason, config.getDouble("experience_modifiers." + reason.name().toLowerCase()));
        }};

        return new MainLevelBean(
            plugin,
            config.getInt("max_level", 0),
            new Expression(Objects.requireNonNull(config.getString("level_to_exp_formula"))),
            new Expression(Objects.requireNonNull(config.getString("exp_to_level_formula"))),
            new Expression(Objects.requireNonNull(config.getString("next_level_exp"))),
            globalModifiers);
    }

    public static @NotNull LevelBean createCateLevelBean(
        @NotNull LevelBean.Category category
    ) {
        // Get config file name
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

        AdventureLevelPlugin plugin = AdventureLevelPlugin.getInstance();

        // Load & apply config file
        File file = plugin.getDataFolder().toPath().resolve("categories").resolve(configFileName).toFile();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        int maxLevel = config.getInt("max_level", 0);
        Expression exp1 = new Expression(Objects.requireNonNull(config.getString("level_to_exp_formula")));
        Expression exp2 = new Expression(Objects.requireNonNull(config.getString("exp_to_level_formula")));
        Expression exp3 = new Expression(Objects.requireNonNull(config.getString("next_level_exp")));

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
