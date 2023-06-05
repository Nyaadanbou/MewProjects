package com.ranull.proxychatbridge.velocity;

import com.google.inject.Inject;
import com.ranull.proxychatbridge.velocity.command.ProxyChatBridgeCommand;
import com.ranull.proxychatbridge.velocity.handler.MessageHandler;
import com.ranull.proxychatbridge.velocity.manager.ConfigManager;
import com.ranull.proxychatbridge.velocity.provider.PlayerDataProvider;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;

@Plugin(
    id = "proxychatbridge",
    dependencies = {
        @Dependency(id = "luckperms", optional = true)
    }
)
public class ProxyChatBridge {

    public static final ChannelIdentifier PLUGIN_MESSAGE_CHANNEL = MinecraftChannelIdentifier.create("mew", "chat");

    // --- Plugin information ---
    private static String VERSION;
    private static String DESCRIPTION;

    // --- Injected instances ---
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataPath;

    // --- All managers ---
    private ConfigManager configManager;
    private MessageHandler messageProcessor;
    private PlayerDataProvider playerDataProvider;

    @Inject
    public ProxyChatBridge(
        ProxyServer server,
        Logger logger,
        PluginContainer container,
        @DataDirectory Path dataPath
    ) {
        this.server = server;
        this.logger = logger;
        this.dataPath = dataPath;

        container.getDescription().getVersion().ifPresent(versionString -> VERSION = versionString);
        container.getDescription().getDescription().ifPresent(descriptionString -> DESCRIPTION = descriptionString);
    }

    // --- Listeners ---

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        configManager = new ConfigManager(this);
        messageProcessor = new MessageHandler(this);
        playerDataProvider = new PlayerDataProvider(this);

        registerChannels();
        registerCommands();
    }

    @Subscribe(order = PostOrder.LAST)
    public void onPluginMessage(PluginMessageEvent event) {
        getMessageHandler().handleIncomingMessage(event);
    }

    @Subscribe
    public void onProxyDisable(ProxyShutdownEvent event) {
        unregisterChannels();
    }

    // --- Getters ---

    public ProxyServer getProxy() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    public MessageHandler getMessageHandler() {
        return messageProcessor;
    }

    public PlayerDataProvider getPlayerDataProvider() {
        return playerDataProvider;
    }

    public File getDataFolder() {
        return dataPath.toFile();
    }

    public ConfigManager getConfig() {
        return configManager;
    }

    public void reloadConfig() {
        configManager.loadConfig();
    }

    public String getVersion() {
        return VERSION;
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    // --- Internal ---

    private void registerChannels() {
        getProxy().getChannelRegistrar().register(PLUGIN_MESSAGE_CHANNEL);
    }

    private void unregisterChannels() {
        getProxy().getChannelRegistrar().unregister(PLUGIN_MESSAGE_CHANNEL);
    }

    private void registerCommands() {
        getProxy().getCommandManager().register("proxychatbridge", new ProxyChatBridgeCommand(this));
    }

}