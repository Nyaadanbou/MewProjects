package cc.mewcraft.reforge;

import cc.mewcraft.reforge.api.ReforgeProvider;
import cc.mewcraft.reforge.command.PluginCommands;
import cc.mewcraft.reforge.hook.MMOItemsReforge;
import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import me.lucko.helper.plugin.ExtendedJavaPlugin;

public class ReforgePlugin extends ExtendedJavaPlugin {
    private PluginCommands pluginCommands;

    @Override protected void enable() {
        // Save default config and reload it
        saveDefaultConfig();
        reloadConfig();

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(ReforgePlugin.class).toInstance(ReforgePlugin.this);
            }
        });

        // Register reforge provider
        if (isPluginPresent("MMOItems")) {
            ReforgeProvider.register(injector.getInstance(MMOItemsReforge.class));
            getSLF4JLogger().info("Registered reforge provider: MMOItems");
        } else {
            getSLF4JLogger().error("Registered reforge provider: NONE");
        }

        // Register commands
        try {
            pluginCommands = injector.getInstance(PluginCommands.class);
            pluginCommands.registerCommands();
        } catch (ConfigurationException | ProvisionException e) {
            getSLF4JLogger().error("Failed to register commands!", e);
        }
    }

    @Override protected void disable() {
        ReforgeProvider.unregister();
        getSLF4JLogger().info("Unregistered reforge provider");
    }
}
