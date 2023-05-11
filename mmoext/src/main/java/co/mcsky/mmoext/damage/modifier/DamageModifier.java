package co.mcsky.mmoext.damage.modifier;

import io.lumine.mythic.lib.damage.DamageType;

public class DamageModifier {

    private final DamageType damageType;
    private final double coefficient;

    public DamageModifier(DamageType damageType, double coefficient) {
        this.damageType = damageType;
        this.coefficient = coefficient;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public double getCoefficient() {
        return coefficient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DamageModifier that = (DamageModifier) o;

        return damageType == that.damageType;
    }

    @Override
    public int hashCode() {
        return damageType.hashCode();
    }

}