package co.mcsky.mewcore.item.impl;

import co.mcsky.mewcore.MewCore;
import co.mcsky.mewcore.item.PluginItem;
import net.leonardo_dgs.interactivebooks.IBook;
import net.leonardo_dgs.interactivebooks.InteractiveBooks;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InteractiveBooksHook extends PluginItem<IBook> {

    @Override
    public @Nullable IBook getPluginItem() {
        IBook book = InteractiveBooks.getBook(getItemId());
        if (book == null) {
            MewCore.logger().severe("[%s] Cannot found item with ID: %s".formatted(getPlugin(), getItemId()));
            return null;
        }
        return book;
    }

    @Override
    public @Nullable ItemStack createItemStack() {
        IBook pluginItem = getPluginItem();
        if (pluginItem == null) return null;
        ItemStack item = pluginItem.getItem();
        item.setAmount(1);
        return item;
    }

    @Override
    public @Nullable ItemStack createItemStack(@NotNull Player player) {
        IBook pluginItem = getPluginItem();
        if (pluginItem == null) return null;
        ItemStack item = pluginItem.getItem(player);
        item.setAmount(1);
        return item;
    }

    @Override
    public boolean matches(@NotNull ItemStack item) {
        IBook book = InteractiveBooks.getBook(item);
        if (book == null) return false;
        return book.getId().equalsIgnoreCase(getItemId());
    }

    @Override
    public boolean belongs(@NotNull ItemStack item) {
        return item.getType() == Material.WRITTEN_BOOK && InteractiveBooks.getBook(item) != null;
    }

    @Override
    public @Nullable String toItemId(@NotNull ItemStack item) {
        IBook book = InteractiveBooks.getBook(item);
        if (book == null) return null;
        return book.getId();
    }

}
