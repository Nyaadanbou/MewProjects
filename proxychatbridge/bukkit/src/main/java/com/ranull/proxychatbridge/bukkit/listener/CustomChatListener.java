package com.ranull.proxychatbridge.bukkit.listener;

import at.helpch.chatchat.api.event.ChatChatEvent;
import at.helpch.chatchat.api.user.ChatUser;
import com.ranull.proxychatbridge.bukkit.ProxyChatBridge;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Note: we can't just listen to the {@link org.bukkit.event.player.AsyncPlayerChatEvent} to get the chat message
 * because {@link AsyncPlayerChatEvent#getMessage()} loses the information that's only available in {@link Component}
 * (such as font and hoverable text).
 * <p>
 * This listener is responsible for:
 * <ol>
 *     <li>Read player chat</li>
 *     <li>Send it to proxy</li>
 * </ol>
 * The proxy should read our message, then forward it to other backed servers.
 */
public class CustomChatListener implements Listener {

    private final ProxyChatBridge plugin;

    public CustomChatListener(ProxyChatBridge plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(ChatChatEvent event) {
        String channelName = event.channel().name();
        if (!channelName.equals(plugin.getForwardChannel()))
            return;

        ChatUser user = event.user();
        Component message = event.message();
        plugin.getMessageProcessor().handleOutgoingMessage(user.player(), message);
    }

}
