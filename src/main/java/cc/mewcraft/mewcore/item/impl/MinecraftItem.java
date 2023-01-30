package cc.mewcraft.mewcore.item.impl;

import cc.mewcraft.mewcore.item.api.PluginItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Just a convenience implementation.
 */
public class MinecraftItem extends PluginItem<Material> {
    public MinecraftItem(final Plugin parent) {
        super(parent);
    }

    @Override public boolean available() {
        return true; // vanilla items always available huh
    }

    @Override public @Nullable Material getPluginItem() {
        return Material.matchMaterial(getItemId());
    }

    @Override public @Nullable ItemStack createItemStack() {
        if (getPluginItem() == null)
            return null;
        return new ItemStack(getPluginItem());
    }

    @Override public @Nullable ItemStack createItemStack(@NotNull final Player player) {
        return createItemStack();
    }

    @Override public boolean matches(@NotNull final ItemStack item) {
        return !item.hasItemMeta() || item.getType() == getPluginItem();
    }

    @Override public boolean belongs(@NotNull final ItemStack item) {
        return true;
    }

    @Override public @Nullable String toItemId(@NotNull final ItemStack item) {
        return item.getType().translationKey();
    }
}
