package cc.mewcraft.mewfishing.loot.impl.loot;

import cc.mewcraft.mewfishing.MewFishing;
import cc.mewcraft.mewfishing.event.FishLootEvent;
import cc.mewcraft.mewfishing.loot.api.Conditioned;
import cc.mewcraft.mewfishing.loot.api.Loot;
import cc.mewcraft.mewfishing.loot.api.LootTable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@DefaultQualifier(NonNull.class)
public class TableLoot extends AbstractLoot<LootTable> {

    private final String tableName;

    public TableLoot(
        double weight,
        String amount,
        List<Conditioned> conditions,
        String tableName
    ) {
        super(weight, amount, conditions);
        this.tableName = tableName;
    }

    @Override public void apply(final FishLootEvent event) {
        @Nullable LootTable table = MewFishing.instance().getFishLootModule().getLootManager().tables().get(tableName);
        Objects.requireNonNull(table, "Failed to load nested table: " + tableName);
        for (int i = 0; i < getAmount(); i++) {
            Collection<Loot> loots = table.drawAll(event);
            loots.forEach(loot -> loot.apply(event));
        }
    }

}
