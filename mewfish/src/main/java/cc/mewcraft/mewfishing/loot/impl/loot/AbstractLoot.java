package cc.mewcraft.mewfishing.loot.impl.loot;

import cc.mewcraft.mewfishing.loot.api.Conditioned;
import cc.mewcraft.mewfishing.loot.api.Loot;
import cc.mewcraft.mewfishing.util.NumberStylizer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

/**
 * Represents a loot item from a loot table.
 */
@DefaultQualifier(NonNull.class)
public abstract class AbstractLoot<T> implements Loot {

    private final double weight; // def = 1
    private final String amount; // def = "1"
    private final List<Conditioned> conditions; // def = empty list

    protected AbstractLoot(double weight, String amount, List<Conditioned> conditions) {
        this.weight = weight;
        this.amount = amount;
        this.conditions = conditions;
    }

    @Override public double getWeight() {
        return weight;
    }

    @Override public int getAmount() {
        return NumberStylizer.getStylizedInt(amount);
    }

    @Override public List<Conditioned> getConditions() {
        return conditions;
    }

}
