package cc.mewcraft.mewcore.item.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Represents a custom item from external plugins.
 *
 * @param <T> the type of Plugin Item
 */
public abstract class PluginItem<T> {

    private final Plugin parent;
    private String plugin;
    private String itemId;

    protected PluginItem(Plugin parent) {
        this.parent = parent;
    }

    /**
     * It's called every time this plugin item instance is instantiated.
     * <p>
     * When it's called, the {@link #plugin} and {@link #itemId} are already set.
     */
    protected void onConstruct() {
    }

    protected void info(String msg) {
        parent.getLogger().info(msg);
    }

    protected void warn(String msg) {
        parent.getLogger().info(msg);
    }

    protected void error(String msg) {
        parent.getLogger().severe(msg);
    }

    /**
     * @return true if this PluginItem is loaded correctly
     */
    abstract public boolean available();

    /**
     * Gets the Plugin ID of this Plugin Item (always lowercase).
     * <p>
     * Usually it's the plugin name with all letters lowercase.
     *
     * @return the Plugin ID
     */
    public @NotNull String getPlugin() {
        return plugin;
    }

    /**
     * Gets the Item ID of this Plugin Item (always lowercase).
     * <p>
     * The format of Item ID is implementation-defined.
     *
     * @return the Item ID
     */
    public @NotNull String getItemId() {
        return itemId;
    }

    /**
     * Sets the Plugin ID of this Plugin Item.
     *
     * @param plugin the Plugin ID
     */
    public void setPlugin(@NotNull String plugin) {
        this.plugin = plugin;
    }

    /**
     * Sets the Item ID of this Plugin Item.
     *
     * @param itemId the Item ID
     */
    public void setItemId(@NotNull String itemId) {
        this.itemId = itemId;
    }

    /**
     * Creates the full config reference of this Plugin Item.
     * <p>
     * A full config reference is in the format of "{pluginId}:{itemId}".
     *
     * @return config reference of this Plugin Item
     */
    public @NotNull String asReference() {
        return getPlugin() + ":" + getItemId();
    }

    /**
     * Gets an instance of the plugin item from the external plugin codebase. The implementation is expected to use
     * {@link #getPlugin()} and {@link #getItemId()} to get the specific plugin item instance from the database of the
     * external plugin.
     *
     * @return the plugin item instance
     */
    abstract public @Nullable T getPluginItem();

    /**
     * @return the ItemStack generated from this plugin item
     */
    abstract public @Nullable ItemStack createItemStack();

    /**
     * @return the ItemStack generated from this plugin item, varying depending on the given player
     */
    abstract public @Nullable ItemStack createItemStack(@NotNull Player player);

    /**
     * Check whether the given ItemStack matches this plugin item.
     *
     * @param item the ItemStack to compare with
     *
     * @return true if the given ItemStack is this plugin item, otherwise false
     */
    abstract public boolean matches(@NotNull ItemStack item);

    /**
     * Check whether the given ItemStack is from this plugin.
     *
     * @param item the ItemStack to compare with
     *
     * @return true if the given ItemStack is from this plugin, otherwise false
     */
    abstract public boolean belongs(@NotNull ItemStack item);

    /**
     * Generate the config reference (only the part {itemId}) from the given ItemStack.
     *
     * @param item the ItemStack to be converted into the config reference
     *
     * @return the config reference from this ItemStack, or <code>null</code> if this is not a custom plugin item
     */
    abstract public @Nullable String toItemId(@NotNull ItemStack item);

}
