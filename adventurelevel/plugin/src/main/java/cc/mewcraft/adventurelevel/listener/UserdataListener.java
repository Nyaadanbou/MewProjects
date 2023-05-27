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
        plugin.getPlayerDataManager()
            .load(event.getPlayer())
            .thenAcceptAsync(playerData -> plugin.getSLF4JLogger().info("Cached userdata: {} ({})", event.getPlayer().getName(), playerData.getUuid()));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onQuit(PlayerQuitEvent event) {
        plugin.getPlayerDataManager()
            .save(event.getPlayer())
            .thenAcceptAsync(playerData -> plugin.getSLF4JLogger().info("Saved userdata: {} ({})", event.getPlayer().getName(), playerData.getUuid()))
            .thenComposeAsync(n -> plugin.getPlayerDataManager().unload(event.getPlayer()));
    }
}
