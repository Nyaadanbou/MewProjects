package cc.mewcraft.mewfishing.loot.impl.loot;

import cc.mewcraft.mewcore.item.api.PluginItem;
import cc.mewcraft.mewfishing.event.FishLootEvent;
import cc.mewcraft.mewfishing.loot.api.Conditioned;
import cc.mewcraft.mewfishing.loot.api.ItemLoot;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

@DefaultQualifier(NonNull.class)
public class PluginItemLoot extends AbstractLoot<ItemStack> implements ItemLoot {

    private final PluginItem<?> pluginItem;

    public PluginItemLoot(
        final double weight,
        final String amount,
        final List<Conditioned> conditions,
        final PluginItem<?> pluginItem
    ) {
        super(weight, amount, conditions);
        this.pluginItem = pluginItem;
    }

    @Override public ItemStack getItem(FishLootEvent event) {
        @Nullable ItemStack item = pluginItem.createItemStack(event.getPlayer());
        if (item == null)
            throw new NullPointerException("item");
        item.setAmount(getAmount());
        return item;
    }

}
