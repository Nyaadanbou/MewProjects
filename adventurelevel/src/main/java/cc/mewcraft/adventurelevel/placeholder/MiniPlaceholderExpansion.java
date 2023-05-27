package cc.mewcraft.adventurelevel.placeholder;

import cc.mewcraft.adventurelevel.data.PlayerDataManager;
import cc.mewcraft.adventurelevel.level.category.LevelBean;
import com.google.inject.Inject;
import io.github.miniplaceholders.api.Expansion;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MiniPlaceholderExpansion {

    private final PlayerDataManager playerDataManager;

    @Inject
    public MiniPlaceholderExpansion(final PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    public void register() {
        Expansion expansion = Expansion.builder("adventurelevel")
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

            // return experience needed to get to next level
            .audiencePlaceholder("experience_needed", (audience, queue, ctx) -> playerDataManager
                .load((Player) audience)
                .thenApplyAsync(playerData -> {
                    LevelBean mainLevel = playerData.getMainLevel();
                    int nextLevel = mainLevel.getLevel() + 1;
                    int expNeeded = mainLevel.calculateNeededExperience(nextLevel);
                    String text = String.valueOf(expNeeded);
                    return Tag.preProcessParsed(text);
                }).join())

            // build the expansion
            .build();

        expansion.register();
    }
}
