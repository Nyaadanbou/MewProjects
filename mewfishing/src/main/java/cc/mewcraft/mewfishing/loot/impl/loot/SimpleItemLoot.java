package cc.mewcraft.mewfishing.loot.impl.loot;

import cc.mewcraft.mewfishing.event.FishLootEvent;
import cc.mewcraft.mewfishing.loot.api.Conditioned;
import cc.mewcraft.mewfishing.loot.api.ItemLoot;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

/**
 * Represents a loot item without any item meta (no display name, lore, enchantments, etc).
 */
@DefaultQualifier(NonNull.class)
public class SimpleItemLoot extends AbstractLoot<ItemStack> implements ItemLoot {

    private final Material mat;

    public SimpleItemLoot(
        double weight,
        String amount,
        List<Conditioned> conditions,
        Material mat
    ) {
        super(weight, amount, conditions);
        this.mat = mat;
    }

    @Override public ItemStack getItem(final FishLootEvent event) {
        ItemStack item = new ItemStack(mat);
        item.setAmount(getAmount());
        return item;
    }

}
