package cc.mewcraft.adventurelevel.level;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import cc.mewcraft.adventurelevel.level.category.LevelBean;
import cc.mewcraft.adventurelevel.level.category.LevelCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

public final class LevelBeanFactory {
    public static @NotNull LevelBean createLevelBean(@NotNull LevelCategory category) {
        AdventureLevelPlugin plugin = AdventureLevelPlugin.getInstance();
        ConfigurationSection config;
        return switch (category) {

            // Create Main Level Bean
            case MAIN: {
                config = Objects.requireNonNull(plugin.getConfig().getConfigurationSection("main_level"));
                yield LevelBeanBuilder.builder(plugin, config).build(category);
            }

            // Create Sub Level Beans
            case PLAYER_DEATH:
            case ENTITY_DEATH:
            case FURNACE:
            case BREED:
            case VILLAGER_TRADE:
            case FISHING:
            case BLOCK_BREAK:
            case EXP_BOTTLE:
            case GRINDSTONE: {
                File file = plugin.getDataFolder().toPath()
                    .resolve("categories")
                    .resolve(category.name().toLowerCase() + ".yml")
                    .toFile();
                config = YamlConfiguration.loadConfiguration(file);
                yield LevelBeanBuilder.builder(plugin, config).build(category);
            }
        };
    }
}
