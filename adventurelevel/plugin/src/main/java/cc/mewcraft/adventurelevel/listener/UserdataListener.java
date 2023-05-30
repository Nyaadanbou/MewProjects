package cc.mewcraft.adventurelevel.listener;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import com.google.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserdataListener implements AutoCloseableListener {

    private final AdventureLevelPlugin plugin;

    @Inject
    public UserdataListener(final AdventureLevelPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onLogin(PlayerJoinEvent event) {
        plugin.getPlayerDataManager().load(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST) // use the lowest priority, so we save it as fast as possible
    public void onQuit(PlayerQuitEvent event) {
        plugin.getPlayerDataManager().unload(event.getPlayer());
    }
}
