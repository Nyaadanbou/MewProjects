package cc.mewcraft.mewfishing.loot.impl.table;

import cc.mewcraft.mewfishing.event.FishLootEvent;
import cc.mewcraft.mewfishing.loot.api.Conditioned;
import cc.mewcraft.mewfishing.loot.api.Loot;
import cc.mewcraft.mewfishing.loot.api.LootTable;
import cc.mewcraft.mewfishing.util.RandomCollection;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@DefaultQualifier(NonNull.class)
public class LootTableImpl implements LootTable {

    private final String name; // it's also the file name (without ".yml" extension)
    private final double chance;
    private final int rolls;
    private final boolean replacement;
    private final List<Conditioned> conditions;
    private final RandomCollection<Loot> lootEntries;

    public LootTableImpl(
        final String name,
        final double chance,
        final int rolls,
        final boolean replacement,
        final List<Conditioned> conditions,
        final RandomCollection<Loot> lootEntries
    ) {
        this.name = name;
        this.chance = chance;
        this.rolls = rolls;
        this.replacement = replacement;
        this.conditions = conditions;
        this.lootEntries = lootEntries;
    }

    @Override public String getName() {
        return name;
    }

    @Override public double getWeight() {
        return chance;
    }

    @Override public List<Conditioned> getConditions() {
        return conditions;
    }

    @Override public int getRolls() {
        return rolls;
    }

    @Override public boolean isReplacement() {
        return replacement;
    }

    @Override public Loot drawOne(final FishLootEvent event) {
        return lootEntries.pick();
    }

    @Override public Collection<Loot> drawMatched(final FishLootEvent event) {
        List<Loot> loots = new ArrayList<>(rolls);

        if (isReplacement()) {
            for (int i = 0; i < getRolls(); i++) {
                Loot pick = lootEntries.pick();
                if (pick.evaluate(event)) {
                    // Only pick it if conditions are met
                    loots.add(pick);
                }
            }
        } else {
            RandomCollection<Loot> copy = lootEntries.copy();
            for (int i = 0; i < getRolls() && !copy.isEmpty(); i++) {
                Loot poll = copy.poll();
                if (poll.evaluate(event)) {
                    // Only pick it if conditions are met
                    loots.add(poll);
                }
            }
        }

        return loots;
    }

}
