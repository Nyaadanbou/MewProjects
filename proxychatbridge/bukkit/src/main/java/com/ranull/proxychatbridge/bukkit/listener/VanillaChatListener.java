package com.ranull.proxychatbridge.bukkit.listener;

import com.ranull.proxychatbridge.bukkit.ProxyChatBridge;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * This listener is used as a fallback when custom chat plugin is not installed on the server.
 *
 * @see CustomChatListener
 */
public class VanillaChatListener implements Listener {

    private final ProxyChatBridge plugin;

    public VanillaChatListener(ProxyChatBridge plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Component message = event.message();
        plugin.getMessageProcessor().handleOutgoingMessage(player, message);
    }

}
