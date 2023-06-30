package cc.mewcraft.reforge.gui;

import cc.mewcraft.mewcore.message.Translations;
import cc.mewcraft.mewcore.util.UtilFile;
import cc.mewcraft.reforge.gui.command.PluginCommands;
import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import xyz.xenondevs.invui.InvUI;

public class ReforgePlugin extends ExtendedJavaPlugin {
    private Injector injector;
    private Translations translations;
    private PluginCommands pluginCommands;

    public Injector getInjector() {
        return injector;
    }

    public Translations getLang() {
        return translations;
    }

    @Override protected void enable() {
        // Save default config files if there are none
        // And then reload config files
        saveDefaultConfig();
        reloadConfig();
        if (!getDataFolder().toPath().resolve("items").toFile().exists()) {
            UtilFile.copyResourcesRecursively(
                getClassLoader().getResource("items"),
                getDataFolder().toPath().resolve("items").toFile()
            );
        }

        // Required if using paper plugin system
        try {
            InvUI.getInstance().setPlugin(this);
        } catch (IllegalStateException ignored) {
            // TODO Remove the try-catch after InvUI#isPluginSet() is introduced
        }

        injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(ReforgePlugin.class).toInstance(ReforgePlugin.this);
                bind(Translations.class).toProvider(() -> new Translations(ReforgePlugin.this, "languages"));
            }
        });

        translations = injector.getInstance(Translations.class);

        // Register commands
        try {
            pluginCommands = injector.getInstance(PluginCommands.class);
            pluginCommands.registerCommands();
        } catch (ConfigurationException | ProvisionException e) {
            getSLF4JLogger().error("Failed to register commands!", e);
        }
    }
}
