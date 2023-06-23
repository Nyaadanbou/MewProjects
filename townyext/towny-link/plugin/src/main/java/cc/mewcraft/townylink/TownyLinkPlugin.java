package cc.mewcraft.townylink;

import cc.mewcraft.mewcore.message.Translations;
import cc.mewcraft.townylink.api.TownyLink;
import cc.mewcraft.townylink.api.TownyLinkProvider;
import cc.mewcraft.townylink.command.PluginCommands;
import cc.mewcraft.townylink.config.LinkConfig;
import cc.mewcraft.townylink.hook.HuskHomesHook;
import cc.mewcraft.townylink.impl.LinkRequestImpl;
import cc.mewcraft.townylink.listener.PlayerListener;
import cc.mewcraft.townylink.sync.TownyListener;
import cc.mewcraft.townylink.teleport.DummyTeleport;
import cc.mewcraft.townylink.teleport.NetworkTeleport;
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
        Injector injector = Guice.createInjector(new MainModule());

        saveDefaultConfig();
        this.config = injector.getInstance(LinkConfig.class);
        this.translations = injector.getInstance(Translations.class);

        // Provide API instance
        TownyLinkProvider.register(bindModule(injector.getInstance(TownyLink.class)));

        // Register listeners
        registerListener(bind(injector.getInstance(PlayerListener.class)));
        if (isPluginPresent("Towny")) {
            registerListener(bindModule(injector.getInstance(TownyListener.class)));
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
            bind(Translations.class).toProvider(() -> new Translations(TownyLinkPlugin.this));

            if (isPluginPresent("HuskHomes")) {
                bind(NetworkTeleport.class).to(HuskHomesHook.class);
            } else {
                bind(NetworkTeleport.class).to(DummyTeleport.class);
            }
        }
    }

}
