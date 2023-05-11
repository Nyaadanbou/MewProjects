package cc.mewcraft.mewcore.hook;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class HookChecker {

    public static boolean hasPlugin(@NotNull HookId pluginName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName.pluginName);
        return plugin != null; // && p.isEnabled();
    }

    public static boolean hasPlaceholderAPI() {
        return hasPlugin(HookId.PLACEHOLDER_API);
    }

    public static boolean hasVault() {
        return hasPlugin(HookId.VAULT);
    }

    public static boolean hasBrewery() {
        return hasPlugin(HookId.BREWERY);
    }

    public static boolean hasInteractiveBooks() {
        return hasPlugin(HookId.INTERACTIVE_BOOKS);
    }

    public static boolean hasItemsAdder() {
        return hasPlugin(HookId.ITEMS_ADDER);
    }

    public static boolean hasMMOItems() {
        return hasPlugin(HookId.MMOITEMS);
    }

    public static boolean hasTowny() {
        return hasPlugin(HookId.TOWNY);
    }

}
