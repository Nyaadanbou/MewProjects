package cc.mewcraft.townyorigin;

import cc.mewcraft.mewcore.message.Translations;
import cc.mewcraft.townyorigin.command.PluginCommands;
import cc.mewcraft.townyorigin.listener.PlayerListener;
import cc.mewcraft.townyorigin.listener.TownyListener;
import cc.mewcraft.townyorigin.placeholder.MiniPlaceholderExpansion;
import cc.mewcraft.townyorigin.placeholder.PAPIPlaceholderExpansion;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import me.lucko.helper.plugin.ExtendedJavaPlugin;

@SuppressWarnings("FieldCanBeLocal")
public class TownyOrigin extends ExtendedJavaPlugin {
    private Injector injector;
    private Translations translations;
    private PluginCommands commands;

    public Translations getLang() {
        return translations;
    }

    @Override protected void enable() {
        injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(TownyOrigin.class).toInstance(TownyOrigin.this);
                bind(Translations.class).toProvider(() -> new Translations(TownyOrigin.this, "languages"));
            }
        });

        translations = injector.getInstance(Translations.class);

        // Register placeholders
        injector.getInstance(MiniPlaceholderExpansion.class).register().bindWith(this);
        injector.getInstance(PAPIPlaceholderExpansion.class).register().bindWith(this);

        // Register listeners
        registerListener(injector.getInstance(PlayerListener.class)).bindWith(this);
        if (isPluginPresent("Towny")) {
            registerListener(injector.getInstance(TownyListener.class)).bindWith(this);
        }

        // Register commands
        try {
            commands = injector.getInstance(PluginCommands.class);
            commands.prepareAndRegister();
        } catch (Exception e) {
            getSLF4JLogger().error("Failed to register commands", e);
        }
    }
}
