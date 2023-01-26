package cc.mewcraft.mewcore.item.impl;

import cc.mewcraft.mewcore.hook.HookChecker;
import cc.mewcraft.mewcore.item.api.PluginItem;
import net.leonardo_dgs.interactivebooks.IBook;
import net.leonardo_dgs.interactivebooks.InteractiveBooks;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InteractiveBooksItem extends PluginItem<IBook> {

    public InteractiveBooksItem(final Plugin parent) {
        super(parent);
    }

    @Override public boolean available() {
        return HookChecker.hasInteractiveBooks();
    }

    @Override
    public @Nullable IBook getPluginItem() {
        IBook book = InteractiveBooks.getBook(getItemId());
        if (book == null) {
            error("[InteractiveBooks] Cannot found item with ID: %s".formatted(getItemId()));
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
