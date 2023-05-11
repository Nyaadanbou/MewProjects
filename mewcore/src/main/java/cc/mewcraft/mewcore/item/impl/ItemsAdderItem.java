package cc.mewcraft.mewcore.item.impl;

import cc.mewcraft.mewcore.hook.HookChecker;
import cc.mewcraft.mewcore.item.api.PluginItem;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderItem extends PluginItem<CustomStack> {

    public ItemsAdderItem(final Plugin parent) {
        super(parent);
    }

    @Override public boolean available() {
        return HookChecker.hasItemsAdder();
    }

    @Override
    public @Nullable CustomStack getPluginItem() {
        return CustomStack.getInstance(getItemId());
    }

    @Override
    public @Nullable ItemStack createItemStack() {
        if (getPluginItem() == null) return null;
        ItemStack itemStack = getPluginItem().getItemStack();
        itemStack.setAmount(1);
        return itemStack;
    }

    @Override
    public @Nullable ItemStack createItemStack(@NotNull Player player) {
        return createItemStack();
    }

    @Override
    public boolean matches(@NotNull ItemStack item) {
        CustomStack other = CustomStack.byItemStack(item);
        if (other == null) return false;
        return getItemId().equalsIgnoreCase(other.getNamespacedID());
    }

    @Override
    public boolean belongs(@NotNull ItemStack item) {
        return CustomStack.byItemStack(item) != null;
    }

    @Override
    public @Nullable String toItemId(@NotNull ItemStack item) {
        CustomStack stack = CustomStack.byItemStack(item);
        if (stack == null) return null;
        return stack.getNamespacedID();
    }

}
