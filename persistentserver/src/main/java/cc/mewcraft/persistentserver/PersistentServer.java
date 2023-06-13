package cc.mewcraft.persistentserver;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
    id = "persistentserver",
    name = "Persistent Server",
    version = "1.0.0",
    authors = {"Nailm"}
)
public class PersistentServer {

    /* public & static fields */
    public static ProxyServer SERVER;
    public static Logger LOGGER;

    /* initialize in constructor */
    private final Path dataDir;

    /* initialize in proxy init event */
    private UserdataManager userDataManager;

    @Inject
    public PersistentServer(ProxyServer server, Logger logger, @DataDirectory Path dataDir) {
        SERVER = server;
        LOGGER = logger;

        this.dataDir = dataDir;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        userDataManager = new UserdataManager(dataDir);
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        userDataManager.cleanup();
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        // Save data to file

        event.getPlayer().getCurrentServer().ifPresent(server ->
            userDataManager.saveLastServer(event.getPlayer().getUniqueId(), server.getServer())
        );
    }

    @Subscribe
    public void onLogin(PlayerChooseInitialServerEvent event) {
        // Make players log back to the server they logged out from

        userDataManager.getLastServer(event.getPlayer().getUniqueId()).ifPresent(event::setInitialServer);
    }
}