package cc.mewcraft.mewfishing.loot.api;

import cc.mewcraft.mewfishing.event.FishLootEvent;
import me.lucko.helper.random.Weighted;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

/**
 * Represents loot in a loot table.
 */
@DefaultQualifier(NonNull.class)
public interface Loot extends Weighted, Conditioned {

    @Override default boolean evaluate(FishLootEvent event) {
        return getConditions().stream().allMatch(condition -> condition.evaluate(event));
    }

    /**
     * Apply this loot to the given event.
     *
     * @param event the event related to this loot
     */
    void apply(FishLootEvent event);

    /**
     * Get the amount of this loot item. The return value may vary on each call.
     *
     * @return the amount of this loot item
     */
    int getAmount();

    /**
     * Get the conditions of this loot.
     *
     * @return the conditions of this loot
     */
    List<Conditioned> getConditions();
}
