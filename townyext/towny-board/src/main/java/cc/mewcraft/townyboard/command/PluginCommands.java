package cc.mewcraft.townyboard.command;

import cc.mewcraft.townyboard.TownyBoardPlugin;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.command.CommandSender;

@Singleton
public class PluginCommands {
    private final TownyBoardPlugin plugin;
    private final CommandRegistry registry;

    @Inject
    public PluginCommands(
        TownyBoardPlugin plugin,
        CommandRegistry registry
    ) {
        this.plugin = plugin;
        this.registry = registry;
    }

    public void registerCommands() {
        // Prepare commands

        registry.prepareCommand(registry
            .commandBuilder("townyboard")
            .literal("reload")
            .permission("townyboard.command.reload")
            .handler(ctx -> {
                CommandSender sender = ctx.getSender();
                plugin.onDisable();
                plugin.onEnable();

                //noinspection UnstableApiUsage
                plugin.getLang().of("msg_plugin_reloaded")
                    .replace("plugin", plugin.getPluginMeta().getName())
                    .replace("version", plugin.getPluginMeta().getVersion())
                    .replace("author", plugin.getPluginMeta().getAuthors().get(0))
                    .send(sender);

            }).build());

        // Register commands

        registry.registerCommands();
    }
}
