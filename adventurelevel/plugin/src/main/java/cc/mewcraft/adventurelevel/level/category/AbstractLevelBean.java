package cc.mewcraft.adventurelevel.level.category;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import cc.mewcraft.adventurelevel.level.modifier.ExperienceModifier;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public abstract class AbstractLevelBean implements LevelBean {

    protected final AdventureLevelPlugin plugin;
    protected final int maxLevel;
    protected int totalExperience;
    protected final Map<String, ExperienceModifier> additiveModifiers;
    protected final Map<String, ExperienceModifier> multiplicativeModifiers;

    /**
     * @see #calculateTotalExperience(int)
     */
    protected final Expression levelToExpFormula;
    /**
     * @see #calculateTotalLevel(int)
     */
    protected final Expression expToLevelFormula;
    /**
     * @see #calculateNeededExperience(int)
     */
    protected final Expression nextLevelFormula;

    public AbstractLevelBean(
        final AdventureLevelPlugin plugin,
        final int maxLevel,
        final Expression levelToExpFormula,
        final Expression expToLevelFormula,
        final Expression nextLevelFormula
    ) {
        this.plugin = plugin;
        this.maxLevel = maxLevel;
        this.levelToExpFormula = levelToExpFormula;
        this.expToLevelFormula = expToLevelFormula;
        this.nextLevelFormula = nextLevelFormula;
        this.additiveModifiers = new HashMap<>();
        this.multiplicativeModifiers = new HashMap<>();
    }

    @Override public void handleEvent(final PlayerPickupExperienceEvent event) {
        this.addExperience(event.getExperienceOrb().getExperience());
    }

    @Override public int calculateTotalExperience(final int level) {
        try {
            return levelToExpFormula
                .with("x", level)
                .evaluate()
                .getNumberValue()
                .intValue();
        } catch (EvaluationException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public int calculateNeededExperience(final int currentLevel) {
        try {
            return nextLevelFormula
                .with("x", currentLevel)
                .evaluate()
                .getNumberValue()
                .intValue();
        } catch (EvaluationException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public double calculateTotalLevel(final int totalExp) {
        try {
            return expToLevelFormula
                .with("x", totalExp)
                .evaluate()
                .getNumberValue()
                .doubleValue();
        } catch (EvaluationException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public int getExperience() {
        return totalExperience;
    }

    @Override public int setExperience(final int value) {
        int oldValue = totalExperience;
        if (value < 0) return oldValue;
        totalExperience = value;
        return oldValue;
    }

    @Override public int addExperience(final int value) {
        // Store old value
        int oldValue = totalExperience;

        if (value < 1) return oldValue; // return earlier for performance reasons

        double additiveMod = 0D;
        double multiplicativeMod = 1D;

        // Sum additive modifiers
        for (final ExperienceModifier mod : this.getExperienceModifiers(ExperienceModifier.Type.ADDITIVE).values()) {
            additiveMod += mod.getValue();
        }
        // Sum multiplicative modifiers
        for (final ExperienceModifier mod : this.getExperienceModifiers(ExperienceModifier.Type.MULTIPLICATIVE).values()) {
            multiplicativeMod *= mod.getValue();
        }

        totalExperience += value * Math.max(0, 1 + additiveMod) * Math.max(0, multiplicativeMod);
        return oldValue;
    }

    @Override public @NotNull Map<String, ExperienceModifier> getExperienceModifiers(ExperienceModifier.Type type) {
        return type == ExperienceModifier.Type.ADDITIVE ? additiveModifiers : multiplicativeModifiers;
    }

    @Override public void addExperienceModifier(final String key, final ExperienceModifier modifier, ExperienceModifier.Type type) {
        this.getExperienceModifiers(type).put(key.toLowerCase(Locale.ROOT), modifier);
    }

    @Override public void removeExperienceModifier(final String key, ExperienceModifier.Type type) {
        this.getExperienceModifiers(type).remove(key.toLowerCase(Locale.ROOT));
    }

    @Override public void clearExperienceModifiers() {
        additiveModifiers.clear();
        multiplicativeModifiers.clear();
    }

    @Override public int getLevel() {
        return (int) Math.min(this.getMaxLevel(), this.calculateTotalLevel(totalExperience)); // cap the level
    }

    @Override public int setLevel(int level) {
        int oldLevel = this.getLevel();
        int newTotalExp = this.calculateTotalExperience(level);
        this.setExperience(newTotalExp);
        return oldLevel;
    }

    @Override public int addLevel(final int level) {
        int oldLevel = this.getLevel();
        int newTotalExp = this.calculateTotalExperience(oldLevel + level);
        this.setExperience(newTotalExp);
        return oldLevel;
    }

    @Override public int getMaxLevel() {
        return maxLevel;
    }
}
