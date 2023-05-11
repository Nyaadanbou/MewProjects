package co.mcsky.mmoext.damage.crit;

import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class CriticalHitModifiers {

    private final Map<CriticalHitType, Double> modifiers;

    public CriticalHitModifiers() {
        modifiers = new EnumMap<>(CriticalHitType.class);
    }

    public void addType(@NotNull CriticalHitType type, double coefficient) {
        modifiers.put(type, coefficient);
    }

    public void removeType(@NotNull CriticalHitType type) {
        modifiers.remove(type);
    }

    public double getType(@NotNull CriticalHitType type) {
        return modifiers.get(type);
    }

    public boolean hasType(@NotNull CriticalHitType type) {
        return modifiers.containsKey(type);
    }

}
