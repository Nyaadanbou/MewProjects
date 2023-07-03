package cc.mewcraft.adventurelevel.data;

import cc.mewcraft.adventurelevel.level.category.Level;
import cc.mewcraft.adventurelevel.level.category.LevelCategory;
import cc.mewcraft.adventurelevel.level.modifier.ExperienceModifier;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DummyPlayerData implements PlayerData {
    private static final DummyLevel DUMMY_LEVEL = new DummyLevel();

    @Override public @NotNull UUID getUuid() {
        return new UUID(0, 0);
    }

    @Override public @NotNull Level getLevel(final LevelCategory category) {
        return DUMMY_LEVEL;
    }

    @Override public @NotNull Map<LevelCategory, Level> asMap() {
        return new HashMap<>();
    }

    @Override public boolean complete() {
        return false; // always incomplete
    }

    @Override public PlayerData markAsIncomplete() {
        return this;
    }

    @Override public PlayerData markAsComplete() {
        return this;
    }

    /**
     * A Dummy Level that does nothing.
     */
    private static class DummyLevel implements Level {
        @Override public void handleEvent(final PlayerPickupExperienceEvent event) {}
        @Override public int calculateTotalExperience(final int level) {return 0;}
        @Override public int calculateNeededExperience(final int currentLevel) {return 0;}
        @Override public double calculateTotalLevel(final int totalExp) {return 0;}
        @Override public int getExperience() {return 0;}
        @Override public int setExperience(final int value) {return 0;}
        @Override public int addExperience(final int value) {return 0;}
        @Override public @NotNull Map<String, ExperienceModifier> getExperienceModifiers(final ExperienceModifier.Type type) {return new HashMap<>();}
        @Override public void addExperienceModifier(final String key, final ExperienceModifier modifier, final ExperienceModifier.Type type) {}
        @Override public void removeExperienceModifier(final String key, final ExperienceModifier.Type type) {}
        @Override public void clearExperienceModifiers() {}
        @Override public int getLevel() {return 0;}
        @Override public int setLevel(final int level) {return 0;}
        @Override public int addLevel(final int level) {return 0;}
        @Override public int getMaxLevel() {return 0;}
    }

}
