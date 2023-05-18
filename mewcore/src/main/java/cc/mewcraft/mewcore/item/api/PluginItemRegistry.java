package cc.mewcraft.mewcore.item.api;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
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
        this.constructors = new LinkedHashMap<>();
    }

    /**
     * Initialize the plugin item registry.
     *
     * @param parent the parent plugin
     */
    public static void init(final Plugin parent) {
        INSTANCE = new PluginItemRegistry(parent);
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
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

    /**
     * Update the given item stack to the latest version.
     *
     * @param item the item stack to refresh
     *
     * @return a new item stack if it's of plugin item, or the original item stack if it's not of plugin item
     */
    @Contract("null -> null; !null -> !null")
    public ItemStack refreshItemStack(ItemStack item) {
        PluginItem<?> pluginItem = fromItemStackNullable(item);
        if (pluginItem != null) {
            ItemStack itemStack = pluginItem.createItemStack();
            if (itemStack != null) {
                itemStack.setAmount(item.getAmount());
                return itemStack;
            }
        }
        return item;
    }

    /**
     * Get the plugin item derived from the given item stack.
     *
     * @param item the item stack to derive
     *
     * @return the plugin item derived from the given item stack
     */
    public @Nullable PluginItem<?> fromItemStackNullable(@Nullable ItemStack item) {
        if (item == null)
            return null;
        for (Map.Entry<String, Supplier<PluginItem<?>>> entry : constructors.entrySet()) {
            PluginItem<?> pluginItem = entry.getValue().get();
            if (!pluginItem.available())
                continue;
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

    /**
     * Get the plugin item derived from the plugin id and item id.
     *
     * @param plugin the plugin id
     * @param itemId the item id
     *
     * @return the plugin item derived from the plugin id and item id
     */
    public @Nullable PluginItem<?> fromReferenceNullable(@Nullable String plugin, @Nullable String itemId) {
        if (plugin == null || itemId == null)
            return null;
        plugin = plugin.toLowerCase();
        itemId = itemId.toLowerCase();
        if (constructors.containsKey(plugin)) {
            PluginItem<?> pluginItem = constructors.get(plugin).get();
            if (!pluginItem.available())
                return null;
            pluginItem.setPlugin(plugin);
            pluginItem.setItemId(itemId);
            pluginItem.onConstruct();
            return pluginItem;
        }
        parent.getLogger().severe("Unsupported plugin item '" + itemId + "' from external plugin '" + plugin + "'."
                                  + " Remove this config line if you don't have the external plugin installed");
        return null;
    }

    /**
     * Get the plugin item from the given item reference.
     *
     * @param reference item reference
     *
     * @return the plugin item from the given reference.
     */
    public @Nullable PluginItem<?> fromReferenceNullable(@Nullable String reference) {
        if (reference == null)
            return null;
        if (!parseble(reference)) {
            parent.getLogger().severe("The format of plugin item ID '" + reference + "' is not correct. Correct format: {plugin}:{itemId}");
            return null;
        }
        String[] split = split(reference);
        return fromReferenceNullable(split[0], split[1]);
    }

    public @NotNull PluginItem<?> fromItemStack(@NotNull ItemStack item) throws NullPointerException {
        return Objects.requireNonNull(fromItemStackNullable(item));
    }

    public @NotNull PluginItem<?> fromReference(@NotNull String plugin, @NotNull String itemId) throws NullPointerException {
        return Objects.requireNonNull(fromReferenceNullable(plugin, itemId));
    }

    public @NotNull PluginItem<?> fromReference(@NotNull String reference) throws NullPointerException {
        return Objects.requireNonNull(fromReferenceNullable(reference));
    }

    public @Nullable String asReference(@Nullable ItemStack item) {
        PluginItem<?> pluginItem = fromItemStackNullable(item);
        return pluginItem == null ? null : pluginItem.asReference();
    }

    /**
     * @param reference a reference to plugin item
     *
     * @return true if specific reference is valid; otherwise false
     */
    public boolean parseble(@Nullable String reference) {
        return reference != null && split(reference).length == 2;
    }

    private @NotNull String[] split(@NotNull String reference) {
        return reference.split(":", 2);
    }

}
