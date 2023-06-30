package cc.mewcraft.reforge.gui.object;

import cc.mewcraft.mewcore.item.api.PluginItem;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.inventory.Inventory;

public class ItemStackIngredient implements ReforgeIngredient {
    final PluginItem<?> item; // Backed by PluginItem
    final int amount; // Required amount (maximum can be greater than 64)

    public ItemStackIngredient(final PluginItem<?> item) {
        this(item, 1);
    }

    public ItemStackIngredient(final PluginItem<?> item, final int amount) {
        this.item = item;
        this.amount = amount;
    }

    public boolean has(Inventory inventory) {
        int leftOver = amount; // remaining amount to check
        for (final ItemStack i : inventory.getUnsafeItems()) {
            if (leftOver <= 0) {
                return true;
            }
            if (i != null && item.matches(i)) {
                leftOver -= i.getAmount();
            }
        }
        return false;
    }

    public void consume(Inventory inventory) {
        int leftOver = amount; // remaining amount to consume
        ItemStack[] items = inventory.getUnsafeItems();
        for (int i = 0; i < items.length; i++) {
            if (leftOver == 0) {
                return;
            }
            ItemStack ui = items[i];
            if (ui != null && item.matches(ui)) {
                int subtract = Math.min(leftOver, ui.getAmount());
                if (ui.subtract(subtract).getAmount() == 0) {
                    inventory.setItemSilently(i, null);
                }
                leftOver -= subtract;
            }
        }
    }
}
