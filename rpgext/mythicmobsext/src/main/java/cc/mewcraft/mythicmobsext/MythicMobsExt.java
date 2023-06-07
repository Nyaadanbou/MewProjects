package cc.mewcraft.mythicmobsext;

import cc.mewcraft.mewcore.message.Translations;
import cc.mewcraft.mythicmobsext.listener.MythicMobListeners;
import cc.mewcraft.mythicmobsext.listener.PlayerAttackListeners;
import cloud.commandframework.Command;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.command.CommandSender;

public class MythicMobsExt extends ExtendedJavaPlugin {

    private Injector injector;
    private Translations translations;
    private MythicMobsConfig config;

    public Injector getInjector() {
        return injector;
    }

    public Translations getLang() {
        return translations;
    }

    public MythicMobsConfig getConfig0() {
        return config;
    }

    @Override protected void enable() {
        translations = new Translations(this, "languages");

        // Configure dependency injector
        injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(MythicMobsExt.class).toInstance(MythicMobsExt.this);
            }
        });

        config = injector.getInstance(MythicMobsConfig.class);

        // Register commands (TODO move to a dedicated class)
        try {
            CommandRegistry commandRegistry = new CommandRegistry(this);
            Command<CommandSender> reloadCommand = commandRegistry
                .commandBuilder("mmext")
                .literal("reload")
                .handler(context -> {
                    onDisable();
                    onEnable();
                    //noinspection UnstableApiUsage
                    getLang().of("msg_reloaded_config").replace("plugin", getPluginMeta().getName()).send(context.getSender());
                }).build();
            commandRegistry.prepareCommand(reloadCommand);
            commandRegistry.registerCommands();
        } catch (Exception e) {
            getSLF4JLogger().error("Failed to initialize commands!");
        }

        // Register listeners
        registerListener(injector.getInstance(MythicMobListeners.class)).bindWith(this);
        registerListener(injector.getInstance(PlayerAttackListeners.class)).bindWith(this);
    }

}
