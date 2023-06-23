package cc.mewcraft.townylink.listener;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.mewcore.util.ServerOriginUtils;
import cc.mewcraft.townylink.TownyLinkPlugin;
import cc.mewcraft.townylink.api.TownyLinkProvider;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

@Singleton
public class PlayerListener implements AutoCloseableListener {
    private final TownyLinkPlugin plugin;

    @Inject
    public PlayerListener(TownyLinkPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {

    }

    @EventHandler
    public void onTest(AsyncChatEvent event) {
        String chat = PlainTextComponentSerializer.plainText().serialize(event.message());

        UUID playerId = event.getPlayer().getUniqueId();
        String originId = ServerOriginUtils.getOriginId(playerId);

        switch (chat) {
            case "test1" -> {
                TownyLinkProvider.get().requestPlayerTown(originId, playerId).thenAcceptAsync(data -> {
                    plugin.getServer().sendMessage(Component.text(data.toString()));
                });
            }
            case "test2" -> {
                TownyLinkProvider.get().requestPlayerNation(originId, playerId).thenAcceptAsync(data -> {
                    plugin.getServer().sendMessage(Component.text(data.toString()));
                });
            }
            case "test3" -> {
                TownyLinkProvider.get().requestServerTown(originId).thenAcceptAsync(data -> {
                    plugin.getServer().sendMessage(Component.text(data.toString()));
                });
            }
            case "test4" -> {
                TownyLinkProvider.get().requestServerNation(originId).thenAcceptAsync(data -> {
                    plugin.getServer().sendMessage(Component.text(data.toString()));
                });
            }
            case "test5" -> {
                TownyLinkProvider.get().requestGlobalTown().thenAcceptAsync(data -> {
                    plugin.getServer().sendMessage(Component.text(data.toString()));
                });
            }
            case "test6" -> {
                TownyLinkProvider.get().requestGlobalNation().thenAcceptAsync(data -> {
                    plugin.getServer().sendMessage(Component.text(data.toString()));
                });
            }
        }
    }
}
