package cc.mewcraft.townylink.listener;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.mewcore.util.ServerOriginUtils;
import cc.mewcraft.townylink.TownyLinkPlugin;
import cc.mewcraft.townylink.api.TownyLink;
import cc.mewcraft.townylink.impl.SimpleTownyLink;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.lucko.helper.messaging.Messenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

@Singleton
public class PlayerListener implements AutoCloseableListener {
    private final TownyLinkPlugin plugin;
    private final TownyLink linkApi;

    @Inject
    public PlayerListener(TownyLinkPlugin plugin) {
        this.plugin = plugin;
        this.linkApi = new SimpleTownyLink(plugin, plugin.getService(Messenger.class));
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {

    }

    @EventHandler
    public void onTest(AsyncChatEvent event) {
        String chat = PlainTextComponentSerializer.plainText().serialize(event.message());
        if (chat.equalsIgnoreCase("test")) {
            UUID playerId = event.getPlayer().getUniqueId();
            String originId = ServerOriginUtils.getOriginId(playerId);
            linkApi.requestPlayerTown(originId, playerId).thenAcceptAsync(data -> {
                plugin.getServer().sendMessage(Component.text(data.toString()));
            });
        }
    }
}
