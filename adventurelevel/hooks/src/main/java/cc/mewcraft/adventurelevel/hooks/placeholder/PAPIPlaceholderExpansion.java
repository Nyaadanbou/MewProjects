package cc.mewcraft.adventurelevel.hooks.placeholder;

import cc.mewcraft.adventurelevel.data.PlayerData;
import cc.mewcraft.adventurelevel.data.PlayerDataManager;
import cc.mewcraft.adventurelevel.level.category.LevelBean;
import cc.mewcraft.adventurelevel.level.category.LevelCategory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.lucko.helper.terminable.Terminable;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Singleton
public class PAPIPlaceholderExpansion implements Terminable {
    private final PlayerDataManager playerDataManager;
    private AdventureLevelExpansion placeholderExpansion;

    @Inject
    public PAPIPlaceholderExpansion(final PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    public PAPIPlaceholderExpansion register() {
        placeholderExpansion = new AdventureLevelExpansion();
        placeholderExpansion.register();
        return this;
    }

    @Override public void close() {
        placeholderExpansion.unregister();
    }

    private class AdventureLevelExpansion extends PlaceholderExpansion {
        @Override public @Nullable String onRequest(final OfflinePlayer player, final @NotNull String params) {
            PlayerData data = playerDataManager.load(player);
            if (!data.complete()) return "";

            LevelBean main = data.getLevelBean(LevelCategory.MAIN);

            return switch (params) {
                case "level" -> String.valueOf(main.getLevel());
                case "level_progress" -> {
                    int currentExp = main.getExperience();
                    double currentLevel = main.calculateTotalLevel(currentExp);
                    yield BigDecimal.valueOf(currentLevel % 1)
                        .scaleByPowerOfTen(2)
                        .setScale(0, RoundingMode.FLOOR)
                        .toPlainString();
                }
                case "experience" -> String.valueOf(main.getExperience());
                case "experience_progress" -> {
                    int exp = main.getExperience();
                    int level = main.getLevel();
                    int levelTotalExp = main.calculateTotalExperience(level);
                    yield String.valueOf(exp - levelTotalExp);
                }
                case "experience_progress_max" -> {
                    int level = main.getLevel();
                    int nextLevelExpNeeded = main.calculateNeededExperience(level + 1);
                    yield String.valueOf(nextLevelExpNeeded);
                }
                default -> "";
            };
        }

        @Override public @NotNull String getIdentifier() {
            return "adventurelevel";
        }

        @Override public @NotNull String getAuthor() {
            return "Nailm";
        }

        @Override public @NotNull String getVersion() {
            return "1.0.0";
        }

        @Override public boolean persist() {
            return true;
        }
    }
}
