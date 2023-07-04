package cc.mewcraft.reforge.gui;

import cc.mewcraft.mewcore.message.Translations;
import cc.mewcraft.mewcore.plugin.MeowJavaPlugin;
import cc.mewcraft.reforge.gui.command.PluginCommands;
import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import net.kyori.adventure.text.minimessage.MiniMessage;
import xyz.xenondevs.inventoryaccess.component.i18n.AdventureComponentLocalizer;
import xyz.xenondevs.inventoryaccess.component.i18n.Languages;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ReforgePlugin extends MeowJavaPlugin {
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
        saveResourceRecursively("lang");
        saveResourceRecursively("item");
        saveDefaultConfig();
        reloadConfig();

        // Configure dependency injector
        injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(ReforgePlugin.class).toInstance(ReforgePlugin.this);
                bind(Translations.class).toProvider(() -> new Translations(ReforgePlugin.this, "lang/message"));
            }
        });

        // Initialize message translations
        translations = injector.getInstance(Translations.class);

        // Initialize translations for InvUI
        try {
            Languages.getInstance().loadLanguage("zh_cn", getDataFolder().toPath().resolve("lang").resolve("modding").resolve("zh_cn.json").toFile(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            getSLF4JLogger().error("Failed to load language files", e);
        }

        // Add support of MiniMessage in InvUI localization config
        AdventureComponentLocalizer.getInstance().setComponentCreator(string -> MiniMessage.miniMessage().deserialize(string));

        // Register commands
        try {
            pluginCommands = injector.getInstance(PluginCommands.class);
            pluginCommands.registerCommands();
        } catch (ConfigurationException | ProvisionException e) {
            getSLF4JLogger().error("Failed to register commands", e);
        }
    }
}
