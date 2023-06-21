package cc.mewcraft.townylink;

import cc.mewcraft.mewcore.message.Translations;
import cc.mewcraft.townylink.command.CommandRegistry;
import cc.mewcraft.townylink.config.LinkConfig;
import cc.mewcraft.townylink.listener.PlayerListener;
import cc.mewcraft.townylink.listener.ServerListener;
import cc.mewcraft.townylink.listener.TownyListener;
import cc.mewcraft.townylink.messager.ConnectorMessenger;
import cc.mewcraft.townylink.messager.EmptyMessenger;
import cc.mewcraft.townylink.messager.Messenger;
import com.google.inject.*;
import de.themoep.connectorplugin.bukkit.BukkitConnectorPlugin;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.plugin.Plugin;

public class TownyLinkPlugin extends ExtendedJavaPlugin {

    private LinkConfig config;
    private Translations translations;
    private CommandRegistry commandRegistry;

    public LinkConfig getLinkConfig() {
        return this.config;
    }

    public Translations getLang() {
        return this.translations;
    }

    @Override protected void enable() {
        saveDefaultConfig();
        this.config = new LinkConfig(this);
        this.translations = new Translations(this);

        Injector injector = Guice.createInjector(new MainModule());

        // Register listeners
        registerListener(injector.getInstance(PlayerListener.class)).bindWith(this);
        registerListener(injector.getInstance(ServerListener.class)).bindWith(this);
        if (isPluginPresent("Towny")) {
            registerListener(injector.getInstance(TownyListener.class)).bindWith(this);
        }

        // Register commands
        try {
            commandRegistry = injector.getInstance(CommandRegistry.class);
            commandRegistry.registerCommands();
        } catch (ConfigurationException | ProvisionException e) {
            getSLF4JLogger().error("Failed to register commands!");
        }
    }

    @Override protected void disable() {

    }

    private class MainModule extends AbstractModule {
        @Override protected void configure() {
            bind(TownyLinkPlugin.class).toInstance(TownyLinkPlugin.this);

            Plugin connectorPlugin = getServer().getPluginManager().getPlugin("ConnectorPlugin");
            if (connectorPlugin == null) {
                bind(Messenger.class).to(EmptyMessenger.class).in(Scopes.SINGLETON);
            } else {
                bind(Messenger.class).to(ConnectorMessenger.class).in(Scopes.SINGLETON);
                bind(BukkitConnectorPlugin.class).toInstance((BukkitConnectorPlugin) connectorPlugin);
            }
        }
    }

}
