package cc.mewcraft.adventurelevel.level.modifier;

@FunctionalInterface
public interface ExperienceModifier {
    enum Type {
        ADDITIVE,
        MULTIPLICATIVE
    }

    double getValue();
}
