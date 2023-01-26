package cc.mewcraft.mewcore.item.api;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class PluginItemRegistry {

    private static PluginItemRegistry INSTANCE;

    private final Map<String, Supplier<PluginItem<?>>> constructors;
    private final Plugin parent;

    private PluginItemRegistry(final Plugin parent) {
        this.parent = parent;
        this.constructors = new HashMap<>();
    }

    public static void init(final Plugin parent) {
        INSTANCE = new PluginItemRegistry(parent);
    }

    public static PluginItemRegistry get() {
        if (INSTANCE == null)
            throw new IllegalStateException("Instance is not initialized yet");
        return INSTANCE;
    }

    public void registerForConfig(@NotNull String pluginId, @NotNull Supplier<PluginItem<?>> constructor) {
        constructors.put(pluginId.toLowerCase(), constructor);
    }

    public void unregisterForConfig(@NotNull String pluginId) {
        constructors.remove(pluginId.toLowerCase());
    }

    public void unregisterAll() {
        constructors.clear();
    }

    public @NotNull ItemStack refreshItemStack(@NotNull ItemStack item) {
        PluginItem<?> pluginItem = fromItemStackNullable(item);
        if (pluginItem != null) {
            ItemStack itemStack = pluginItem.createItemStack();
            if (itemStack != null) {
                return itemStack;
            }
        }
        return item;
    }

    public @Nullable PluginItem<?> fromItemStackNullable(@Nullable ItemStack item) {
        if (item == null) return null;
        for (Map.Entry<String, Supplier<PluginItem<?>>> entry : constructors.entrySet()) {
            PluginItem<?> pluginItem = entry.getValue().get();
            if (!pluginItem.available()) return null;
            if (pluginItem.belongs(item)) {
                String plugin = Objects.requireNonNull(entry.getKey()).toLowerCase(Locale.ROOT);
                String itemId = Objects.requireNonNull(pluginItem.toItemId(item)).toLowerCase(Locale.ROOT);
                pluginItem.setPlugin(plugin);
                pluginItem.setItemId(itemId);
                pluginItem.onConstruct();
                return pluginItem;
            }
        }
        return null;
    }

    public @Nullable PluginItem<?> fromReferenceNullable(@Nullable String plugin, @Nullable String itemId) {
        if (plugin == null || itemId == null) return null;
        plugin = plugin.toLowerCase();
        itemId = itemId.toLowerCase();
        if (constructors.containsKey(plugin)) {
            PluginItem<?> pluginItem = constructors.get(plugin).get();
            if (!pluginItem.available()) return null;
            pluginItem.setPlugin(plugin);
            pluginItem.setItemId(itemId);
            pluginItem.onConstruct();
            return pluginItem;
        }
        parent.getLogger().severe("Unsupported plugin item '" + itemId + "' from external plugin '" + plugin + "'."
                                  + " Remove this config line if you don't have the external plugin installed");
        return null;
    }

    public @Nullable PluginItem<?> fromReferenceNullable(@Nullable String reference) {
        if (!isPluginItemId(reference)) {
            parent.getLogger().severe("The format of plugin item ID '" + reference + "' is not correct. Correct format: {plugin}:{itemId}");
            return null;
        }
        String[] split = toPluginItemId(reference);
        return fromReference(split[0], split[1]);
    }

    public @NotNull PluginItem<?> fromItemStack(@NotNull ItemStack item) {
        PluginItem<?> pluginItem = fromItemStackNullable(item);
        if (pluginItem == null) throw new NullPointerException();
        return pluginItem;
    }

    public @NotNull PluginItem<?> fromReference(@NotNull String plugin, @NotNull String itemId) {
        return Objects.requireNonNull(fromReferenceNullable(plugin, itemId));
    }

    public @NotNull PluginItem<?> fromReference(@NotNull String reference) {
        return Objects.requireNonNull(fromReferenceNullable(reference));
    }

    public @Nullable String toReference(@Nullable ItemStack item) {
        PluginItem<?> pluginItem = fromItemStackNullable(item);
        if (pluginItem == null) return null;
        return pluginItem.getPlugin() + ":" + pluginItem.getItemId();
    }

    public boolean isPluginItemId(@Nullable String reference) {
        return reference != null && toPluginItemId(reference).length == 2;
    }

    private @NotNull String[] toPluginItemId(@NotNull String reference) {
        return reference.split(":", 2);
    }

}
