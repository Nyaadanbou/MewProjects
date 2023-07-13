package cc.mewcraft.enchantment.gui;

import cc.mewcraft.enchantment.gui.adapter.ExcellentEnchantAdapter;
import cc.mewcraft.enchantment.gui.command.PluginCommands;
import cc.mewcraft.mewcore.message.Translations;
import cc.mewcraft.mewcore.plugin.MeowJavaPlugin;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.kyori.adventure.text.minimessage.MiniMessage;
import xyz.xenondevs.inventoryaccess.component.i18n.AdventureComponentLocalizer;
import xyz.xenondevs.inventoryaccess.component.i18n.Languages;
import xyz.xenondevs.invui.window.Window;
import xyz.xenondevs.invui.window.WindowManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EnchantGuiPlugin extends MeowJavaPlugin {
    private Injector injector;
    private Translations translations;

    public Injector getInjector() {
        return injector;
    }

    public Translations getLang() {
        return translations;
    }

    @Override protected void enable() {
        saveResourceRecursively("lang");
        saveDefaultConfig();
        reloadConfig();

        injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(EnchantGuiPlugin.class).toInstance(EnchantGuiPlugin.this);
                bind(Translations.class).toProvider(() -> new Translations(EnchantGuiPlugin.this, "lang/message"));
            }
        });

        // Load message languages
        translations = injector.getInstance(Translations.class);

        // Load modding languages
        try {
            Languages.getInstance().loadLanguage("zh_cn", getBundledFile("lang/modding/zh_cn.json"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            getSLF4JLogger().error("Failed to load language files", e);
        }

        // Set MiniMessage parser
        AdventureComponentLocalizer.getInstance().setComponentCreator(MiniMessage.miniMessage()::deserialize);

        // Initialize commands
        try {
            PluginCommands pluginCommands = injector.getInstance(PluginCommands.class);
            pluginCommands.prepareAndRegister();
        } catch (Exception e) {
            getSLF4JLogger().error("Failed to initialize commands", e);
        }

        if (isPluginPresent("ExcellentEnchants")) {
            injector.getInstance(ExcellentEnchantAdapter.class).initialize();
        } else {
            getSLF4JLogger().error("There is no UiEnchant adapter available!");
        }
    }

    @Override protected void disable() {
        getSLF4JLogger().info("Closing all windows...");
        WindowManager.getInstance().getWindows().forEach(Window::close);
    }
}
