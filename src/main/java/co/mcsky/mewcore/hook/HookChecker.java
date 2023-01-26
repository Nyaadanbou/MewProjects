package co.mcsky.mewcore.hook;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import static co.mcsky.mewcore.hook.HookId.*;

public class HookChecker {

    public static boolean hasPlugin(@NotNull HookId pluginName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName.pluginName);
        return plugin != null; // && p.isEnabled();
    }

    public static boolean hasPlaceholderAPI() {
        return hasPlugin(PLACEHOLDER_API);
    }

    public static boolean hasVault() {
        return hasPlugin(VAULT);
    }

    public static boolean hasBrewery() {
        return hasPlugin(BREWERY);
    }

    public static boolean hasInteractiveBooks() {
        return hasPlugin(INTERACTIVE_BOOKS);
    }

    public static boolean hasItemsAdder() {
        return hasPlugin(ITEMS_ADDER);
    }

    public static boolean hasMMOItems() {
        return hasPlugin(MMOITEMS);
    }

}
