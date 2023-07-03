package cc.mewcraft.adventurelevel.hooks.placeholder;

import cc.mewcraft.adventurelevel.data.PlayerData;
import cc.mewcraft.adventurelevel.data.PlayerDataManager;
import cc.mewcraft.adventurelevel.level.category.LevelBean;
import cc.mewcraft.adventurelevel.level.category.LevelCategory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.miniplaceholders.api.Expansion;
import me.lucko.helper.terminable.Terminable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Singleton
public class MiniPlaceholderExpansion implements Terminable {
    private static final Tag EMPTY_TAG = Tag.selfClosingInserting(Component.empty());
    private final PlayerDataManager playerDataManager;
    private Expansion expansion;

    @Inject
    public MiniPlaceholderExpansion(final PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    public MiniPlaceholderExpansion register() {
        expansion = Expansion.builder("adventurelevel")
            .filter(Player.class)

            // return current main level
            .audiencePlaceholder("level", (audience, queue, ctx) -> {
                PlayerData data = playerDataManager.load((Player) audience);
                if (!data.complete()) return EMPTY_TAG;
                String level = String.valueOf(data.getLevelBean(LevelCategory.MAIN).getLevel());
                return Tag.preProcessParsed(level);
            })

            // return progress to next level in percent (1-99)
            .audiencePlaceholder("level_progress", (audience, queue, ctx) -> {
                PlayerData data = playerDataManager.load((Player) audience);
                if (!data.complete()) return EMPTY_TAG;
                LevelBean levelBean = data.getLevelBean(LevelCategory.MAIN);
                int currentExp = levelBean.getExperience();
                double currentLevel = levelBean.calculateTotalLevel(currentExp);
                String text = BigDecimal.valueOf(currentLevel % 1)
                    .scaleByPowerOfTen(2)
                    .setScale(0, RoundingMode.FLOOR)
                    .toPlainString();
                return Tag.preProcessParsed(text);
            })

            // return total experience
            .audiencePlaceholder("experience", (audience, queue, ctx) -> {
                PlayerData data = playerDataManager.load((Player) audience);
                if (!data.complete()) return EMPTY_TAG;
                String level = String.valueOf(data.getLevelBean(LevelCategory.MAIN).getExperience());
                return Tag.preProcessParsed(level);
            })

            // return experience gained for current progress
            .audiencePlaceholder("experience_progress", (audience, queue, ctx) -> {
                PlayerData data = playerDataManager.load((Player) audience);
                if (!data.complete()) return EMPTY_TAG;
                LevelBean mainLevel = data.getLevelBean(LevelCategory.MAIN);
                int exp = mainLevel.getExperience();
                int level = mainLevel.getLevel();
                int levelTotalExp = mainLevel.calculateTotalExperience(level);
                String text = String.valueOf(exp - levelTotalExp);
                return Tag.preProcessParsed(text);
            })

            // return experience needed to get to next level from current level
            .audiencePlaceholder("experience_progress_max", (audience, queue, ctx) -> {
                PlayerData data = playerDataManager.load((Player) audience);
                if (!data.complete()) return EMPTY_TAG;
                LevelBean mainLevel = data.getLevelBean(LevelCategory.MAIN);
                int level = mainLevel.getLevel();
                int nextLevelExpNeeded = mainLevel.calculateNeededExperience(level + 1);
                String text = String.valueOf(nextLevelExpNeeded);
                return Tag.preProcessParsed(text);
            })

            // build the expansion
            .build();

        expansion.register();

        return this;
    }

    @Override public void close() {
        expansion.unregister();
    }
}
