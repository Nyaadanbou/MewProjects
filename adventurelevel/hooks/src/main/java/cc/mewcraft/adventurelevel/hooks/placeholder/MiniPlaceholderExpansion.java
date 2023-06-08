package cc.mewcraft.adventurelevel.hooks.placeholder;

import cc.mewcraft.adventurelevel.data.PlayerDataManager;
import cc.mewcraft.adventurelevel.level.category.LevelBean;
import com.google.inject.Inject;
import io.github.miniplaceholders.api.Expansion;
import me.lucko.helper.terminable.Terminable;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MiniPlaceholderExpansion implements Terminable {

    private final PlayerDataManager playerDataManager;
    private @MonotonicNonNull Expansion expansion;

    @Inject
    public MiniPlaceholderExpansion(final PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    public MiniPlaceholderExpansion register() {
        expansion = Expansion.builder("adventurelevel")
            .filter(Player.class)

            // return current main level
            .audiencePlaceholder("level", (audience, queue, ctx) -> playerDataManager
                .load((Player) audience)
                .thenApplyAsync(playerData -> {
                    String level = String.valueOf(playerData.getMainLevel().getLevel());
                    return Tag.preProcessParsed(level);
                }).join())

            // return progress to next level in percent (1-99)
            .audiencePlaceholder("level_progress", (audience, queue, ctx) -> playerDataManager
                .load((Player) audience)
                .thenApplyAsync(playerData -> {
                    LevelBean levelBean = playerData.getMainLevel();
                    int currentExp = levelBean.getExperience();
                    double currentLevel = levelBean.calculateTotalLevel(currentExp);
                    String text = BigDecimal.valueOf(currentLevel % 1)
                        .scaleByPowerOfTen(2)
                        .setScale(0, RoundingMode.FLOOR)
                        .toPlainString();
                    return Tag.preProcessParsed(text);
                }).join())

            // return total experience
            .audiencePlaceholder("experience", (audience, queue, ctx) -> playerDataManager
                .load((Player) audience)
                .thenApplyAsync(playerData -> {
                    String level = String.valueOf(playerData.getMainLevel().getExperience());
                    return Tag.preProcessParsed(level);
                }).join())

            // return experience gained for current progress
            .audiencePlaceholder("experience_progress", (audience, queue, ctx) -> playerDataManager
                .load((Player) audience)
                .thenApplyAsync(playerData -> {
                    LevelBean mainLevel = playerData.getMainLevel();
                    int exp = mainLevel.getExperience();
                    int level = mainLevel.getLevel();
                    int levelTotalExp = mainLevel.calculateTotalExperience(level);
                    String text = String.valueOf(exp - levelTotalExp);
                    return Tag.preProcessParsed(text);
                }).join())

            // return experience needed to get to next level from current level
            .audiencePlaceholder("experience_progress_max", (audience, queue, ctx) -> playerDataManager
                .load((Player) audience)
                .thenApplyAsync(playerData -> {
                    LevelBean mainLevel = playerData.getMainLevel();
                    int level = mainLevel.getLevel();
                    int nextLevelExpNeeded = mainLevel.calculateNeededExperience(level + 1);
                    String text = String.valueOf(nextLevelExpNeeded);
                    return Tag.preProcessParsed(text);
                }).join())

            // build the expansion
            .build();

        expansion.register();

        return this;
    }

    @Override public void close() {
        expansion.unregister();
    }
}
