package cc.mewcraft.mewhelp;

import cc.mewcraft.mewcore.message.Translations;
import cc.mewcraft.mewhelp.command.CommandRegistry;
import cc.mewcraft.mewhelp.command.HelpTopicArgument;
import cc.mewcraft.mewhelp.object.HelpTopic;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.command.CommandSender;

public final class MewHelp extends ExtendedJavaPlugin {
    public static MewHelp INSTANCE;

    private TopicManager config;
    private Translations translations;

    public static MewHelp getInstance() {
        return INSTANCE;
    }

    @Override
    protected void enable() {
        INSTANCE = this;

        translations = new Translations(this, "languages");

        config = new TopicManager(this);
        config.loadConfig();

        try {
            CommandRegistry commandRegistry = bind(new CommandRegistry(this));

            // Prepare command: /help <topic>
            commandRegistry.prepareCommand(commandRegistry
                .commandBuilder("help")
                .argument(HelpTopicArgument.of("topic"))
                .handler(ctx -> {
                    HelpTopic topic = ctx.get("topic");
                    CommandSender sender = ctx.getSender();
                    topic.components().forEach(sender::sendMessage);
                }).build()
            );
            // Prepare command: /help reload
            commandRegistry.prepareCommand(commandRegistry
                .commandBuilder("help")
                .literal("reload")
                .permission("mewhelp.admin")
                .handler(ctx -> {
                    onDisable();
                    onEnable();
                    //noinspection UnstableApiUsage
                    getLanguages().of("msg_reloaded_config")
                        .replace("plugin", getPluginMeta().getName())
                        .replace("author", getPluginMeta().getAuthors().get(0))
                        .send(ctx.getSender());
                }).build()
            );

            // Register all commands
            commandRegistry.registerCommands();
        } catch (Exception e) {
            getSLF4JLogger().error("Failed to register commands!");
        }
    }

    @Override
    protected void disable() {}

    public TopicManager getTopicManager() {
        return config;
    }

    public Translations getLanguages() {
        return translations;
    }

}
