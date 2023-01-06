package co.mcsky.mewcore.item.impl;

import co.mcsky.mewcore.item.PluginItem;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderHook extends PluginItem<CustomStack> {

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
        CustomStack otherCustomStack = CustomStack.byItemStack(item);
        if (otherCustomStack == null) return false;
        return getItemId().equalsIgnoreCase(otherCustomStack.getNamespacedID());
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
