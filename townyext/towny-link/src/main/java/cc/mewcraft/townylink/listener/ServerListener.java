package cc.mewcraft.townylink.listener;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.mewcore.util.UtilComponent;
import cc.mewcraft.townylink.TownyLink;
import cc.mewcraft.townylink.messager.Action;
import cc.mewcraft.townylink.messager.Messenger;
import cc.mewcraft.townylink.util.TownyUtils;
import com.google.inject.Inject;
import me.lucko.helper.Schedulers;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerLoadEvent;

import java.util.concurrent.TimeUnit;

public class ServerListener implements AutoCloseableListener {

    private final TownyLink plugin;
    private final Messenger messenger;

    @Inject
    public ServerListener(final TownyLink plugin, final Messenger messenger) {
        this.plugin = plugin;
        this.messenger = messenger;
    }

    @EventHandler
    public void onServerStart(ServerLoadEvent event) {

        if (event.getType() != ServerLoadEvent.LoadType.STARTUP)
            return;

        Schedulers.async().runLater(() -> {
            this.plugin.getComponentLogger().info(UtilComponent.asComponent(
                "<aqua>Synchronizing Towny data with other servers...")
            );
            this.messenger.sendMessage(Action.FETCH_TOWN);
            this.messenger.sendMessage(Action.FETCH_NATION);
            this.messenger.sendMessage(Action.ADD_TOWN, TownyUtils.getAllTownNames());
            this.messenger.sendMessage(Action.ADD_NATION, TownyUtils.getALlNationNames());
            this.plugin.getComponentLogger().info(UtilComponent.asComponent(
                "<aqua>Synchronization completed!")
            );
        }, 5, TimeUnit.SECONDS);

    }

}
