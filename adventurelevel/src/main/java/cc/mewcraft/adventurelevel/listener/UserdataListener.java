package cc.mewcraft.adventurelevel.listener;

import cc.mewcraft.adventurelevel.AdventureLevel;
import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import com.google.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserdataListener implements AutoCloseableListener {

    private final AdventureLevel plugin;

    @Inject
    public UserdataListener(final AdventureLevel plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        plugin.getPlayerDataManager()
            .load(event.getPlayer())
            .thenAcceptAsync(playerData -> plugin.getSLF4JLogger().info("Loaded adventure level data: " + playerData.getUuid()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getPlayerDataManager().save(event.getPlayer());
        plugin.getPlayerDataManager().unload(event.getPlayer());
        plugin.getSLF4JLogger().info("Saved adventure level data: " + event.getPlayer().getUniqueId());
    }
}
