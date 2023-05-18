package cc.mewcraft.mewcore.item.impl;

import cc.mewcraft.mewcore.hook.HookChecker;
import cc.mewcraft.mewcore.item.api.PluginItem;
import io.lumine.mythic.lib.api.item.NBTItem;
import me.lucko.helper.Schedulers;
import me.lucko.helper.promise.Promise;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class MMOItemsItem extends PluginItem<MMOItemTemplate> {

    public MMOItemsItem(final Plugin parent) {
        super(parent);
    }

    @Override public boolean available() {
        return HookChecker.hasMMOItems();
    }

    // These two methods `getItemStack()` and `getItemStack(Player)`
    // may generate different item on each call if this item enabled RNG stats
    // See: https://gitlab.com/phoenix-dvpmt/mmoitems/-/wikis/Item-Stats-and-Options

    @Override
    public @Nullable MMOItemTemplate getPluginItem() {
        String[] itemId = getItemId().toUpperCase(Locale.ROOT).split(":");
        Type type = MMOItems.plugin.getTypes().get(itemId[0]);
        if (type == null) {
            error("[MMOItems] Could not found item type: %s".formatted(itemId[0]));
            return null;
        }
        MMOItemTemplate mmoItemTemplate = MMOItems.plugin.getTemplates().getTemplate(type, itemId[1]);
        if (mmoItemTemplate != null) {
            return mmoItemTemplate;
        } else {
            error("[MMOItems] Could not found item: %s (type: %s)".formatted(itemId[1], itemId[0]));
            return null;
        }
    }

    @Override
    public @Nullable ItemStack createItemStack() {
        MMOItemTemplate pluginItem = getPluginItem();
        if (pluginItem == null) return null;

        if (Bukkit.isPrimaryThread()) {
            return pluginItem.newBuilder().build().newBuilder().build();
        } else {
            Promise<ItemStack> call = Schedulers.sync().call(pluginItem.newBuilder().build().newBuilder()::build);
            try {
                return call.get();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public @Nullable ItemStack createItemStack(@NotNull Player player) {
        MMOItemTemplate pluginItem = getPluginItem();
        if (pluginItem == null) return null;

        if (Bukkit.isPrimaryThread()) {
            return pluginItem.newBuilder(player).build().newBuilder().build();
        } else {
            Promise<ItemStack> call = Schedulers.sync().call(pluginItem.newBuilder(player).build().newBuilder()::build);
            try {
                return call.get();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public boolean matches(@NotNull ItemStack item) {
        NBTItem nbtItem = NBTItem.get(item);
        if (!nbtItem.hasType()) return false;
        String type = nbtItem.getType();
        String id = nbtItem.getString("MMOITEMS_ITEM_ID");
        return getItemId().equalsIgnoreCase(type + ":" + id);
    }

    @Override
    public boolean belongs(@NotNull ItemStack item) {
        return NBTItem.get(item).hasType();
    }

    @Override
    public @Nullable String toItemId(@NotNull ItemStack item) {
        NBTItem nbtItem = NBTItem.get(item);
        if (!nbtItem.hasType()) return null;
        String type = nbtItem.getType();
        String id = nbtItem.getString("MMOITEMS_ITEM_ID");
        return (type + ":" + id).toLowerCase(Locale.ROOT);
    }

}
