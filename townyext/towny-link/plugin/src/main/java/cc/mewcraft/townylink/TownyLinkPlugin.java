package cc.mewcraft.townylink;

import cc.mewcraft.mewcore.message.Translations;
import cc.mewcraft.townylink.api.TownyLink;
import cc.mewcraft.townylink.api.TownyLinkProvider;
import cc.mewcraft.townylink.command.PluginCommands;
import cc.mewcraft.townylink.config.LinkConfig;
import cc.mewcraft.townylink.impl.LinkRequestImpl;
import cc.mewcraft.townylink.listener.PlayerListener;
import cc.mewcraft.townylink.sync.TownyListener;
import com.google.inject.*;
import me.lucko.helper.plugin.ExtendedJavaPlugin;

public class TownyLinkPlugin extends ExtendedJavaPlugin {

    private LinkConfig config;
    private Translations translations;
    private PluginCommands pluginCommands;

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

        // Provide API instance
        TownyLinkProvider.register(injector.getInstance(TownyLink.class));

        // Register listeners
        registerListener(injector.getInstance(PlayerListener.class)).bindWith(this);
        if (isPluginPresent("Towny")) {
            registerListener(injector.getInstance(TownyListener.class)).bindWith(this);
        }

        // Register commands
        try {
            pluginCommands = injector.getInstance(PluginCommands.class);
            pluginCommands.registerCommands();
        } catch (ConfigurationException | ProvisionException e) {
            getSLF4JLogger().error("Failed to register commands!", e);
        }
    }

    private class MainModule extends AbstractModule {
        @Override protected void configure() {
            bind(TownyLinkPlugin.class).toInstance(TownyLinkPlugin.this);
            bind(TownyLink.class).to(LinkRequestImpl.class);
        }
    }

}
