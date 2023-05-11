package co.mcsky.mmoext;

import org.bukkit.Bukkit;

public class SimpleHook {

    public static boolean hasPlugin(HookId id) {
        return Bukkit.getServer().getPluginManager().getPlugin(id.plugin) != null;
    }

}
