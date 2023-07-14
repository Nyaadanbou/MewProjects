package cc.mewcraft.mewfishing.loot.api;

import cc.mewcraft.mewfishing.event.FishLootEvent;
import me.lucko.helper.random.Weighted;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Collection;
import java.util.List;

/**
 * Represents a loot table.
 */
@DefaultQualifier(NonNull.class)
public interface LootTable extends Weighted, Conditioned {

    @Override default boolean evaluate(FishLootEvent event) {
        return getConditions().stream().allMatch(condition -> condition.evaluate(event));
    }

    /**
     * Get the name of the loot table.
     *
     * @return the name of the loot table
     */
    String getName();

    /**
     * Get the conditions that has to be met for this loot table to be selected.
     *
     * @return the conditions that has to be met for this loot table to be selected
     */
    List<Conditioned> getConditions();

    /**
     * Get the number of items chosen.
     *
     * @return the number of items chosen
     */
    int getRolls();

    /**
     * Get whether the loots should be drawn with-replacement / without-replacement.
     *
     * @return true if the loots should be drawn with-replacement; false if the loots should be drawn
     * without-replacement.
     */
    boolean isReplacement();

    /**
     * Draw a loot item from this loot table.
     *
     * @param event the event triggering this loot
     *
     * @return a random loot item from this loot table
     */
    Loot drawOne(FishLootEvent event);

    /**
     * Draw {@literal N} loots from this loot table, where {@literal N} is obtained by {@link #getRolls()}.
     *
     * @param event the event triggering this loot
     *
     * @return {@literal N} random loots from this loot table
     */
    Collection<Loot> drawMatched(FishLootEvent event);

    /**
     * Get the weight where this loot table will be selected from all tables.
     *
     * @return the weight where this loot table will be selected
     */
    @Override double getWeight();
}
