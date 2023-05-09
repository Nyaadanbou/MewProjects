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
        return Material.matchMaterial(this.getItemId());
    }

    @Override public @Nullable ItemStack createItemStack() {
        if (this.getPluginItem() == null)
            return null;
        return new ItemStack(this.getPluginItem());
    }

    @Override public @Nullable ItemStack createItemStack(final @NotNull Player player) {
        return this.createItemStack();
    }

    @Override public boolean matches(final @NotNull ItemStack item) {
        return !item.hasItemMeta() && item.getType() == this.getPluginItem();
    }

    @Override public boolean belongs(final @NotNull ItemStack item) {
        return !item.hasItemMeta();
    }

    @Override public @Nullable String toItemId(final @NotNull ItemStack item) {
        return item.getType().getKey().value();
    }
}
