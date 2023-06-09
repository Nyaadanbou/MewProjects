package cc.mewcraft.adventurelevel.level.category;

import cc.mewcraft.adventurelevel.level.modifier.ExperienceModifier;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * The number of levels is just a final result, which is derived from the backed experience value.
 * <p>
 * Implementation Notes: only experience value should be stored as a value source, the number of levels should be
 * derived from the {@link #calculateTotalLevel(int)}. Implementation may cache the number of levels if the calculation
 * is expensive.
 */
public interface LevelBean {
    void handleEvent(PlayerPickupExperienceEvent event);

    /**
     * @param level a specific level
     *
     * @return how much experience value has been collected to reach the given level
     */
    int calculateTotalExperience(int level);

    /**
     * @param currentLevel the current level
     *
     * @return how many experience value needed to get to the next level from the given current level
     */
    int calculateNeededExperience(int currentLevel);

    /**
     * Side Note: It's an inverse of {@link #calculateTotalExperience(int)}.
     *
     * @param totalExp the total experience value
     *
     * @return the number of levels derived from the given total experience value. The returned number of levels is a
     * double and the decimal part is essentially the progress to the next level, with <b>0</b> indicating zero progress
     * and <b>1</b> indicating full progress
     */
    double calculateTotalLevel(int totalExp);

    /**
     * @return the current experience value
     */
    int getExperience();

    /**
     * @param value the new experience value
     *
     * @return the old experience value
     */
    int setExperience(int value);

    /**
     * @param value the added experience value
     *
     * @return the old experience value
     */
    int addExperience(int value);

    /**
     * @return the modifier applied when some experience value is obtained
     */
    @NotNull Map<String, ExperienceModifier> getExperienceModifiers(ExperienceModifier.Type type);

    /**
     * @param key      the key of the experience modifier
     * @param modifier the experience modifier applied when some experience value is obtained
     */
    void addExperienceModifier(String key, ExperienceModifier modifier, ExperienceModifier.Type type);

    /**
     * @param key the key of which the experience modifier should be removed
     */
    void removeExperienceModifier(String key, ExperienceModifier.Type type);

    /**
     * Clear all experience modifiers.
     */
    void clearExperienceModifiers();

    /**
     * @return the current number of levels
     */
    int getLevel();

    /**
     * @param level the new number of levels
     *
     * @return the old number of levels
     */
    int setLevel(int level);

    /**
     * @param level the number of levels to be added
     *
     * @return the old number of levels
     */
    int addLevel(int level);

    /**
     * @return the max number of levels
     */
    int getMaxLevel();

    /**
     * Sets the experience value of this bean and returns itself.
     *
     * @param value the new experience value
     *
     * @return this instance
     */
    @SuppressWarnings("unchecked")
    default <T extends LevelBean> T withExperience(int value) {
        this.setExperience(value);
        return (T) this;
    }

    /**
     * Sets the number of levels of this bean and returns itself.
     *
     * @param level the new number of levels
     *
     * @return this instance
     */
    @SuppressWarnings("unchecked")
    default <T extends LevelBean> T withLevel(int level) {
        this.setLevel(level);
        return (T) this;
    }
}
