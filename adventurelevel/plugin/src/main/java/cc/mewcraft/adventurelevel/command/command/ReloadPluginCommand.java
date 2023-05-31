package cc.mewcraft.adventurelevel.command.command;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import cc.mewcraft.adventurelevel.command.AbstractCommand;
import cc.mewcraft.adventurelevel.command.CommandManager;
import cloud.commandframework.Command;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadPluginCommand extends AbstractCommand {
    public ReloadPluginCommand(final AdventureLevelPlugin plugin, final CommandManager manager) {
        super(plugin, manager);
    }

    @Override public void register() {
        Command<CommandSender> reloadCommand = manager.commandBuilder("adventurelevel")
            .literal("reload")
            .permission("adventurelevel.command.admin")
            .handler(context -> {
                plugin.onDisable();
                plugin.onEnable();

                //noinspection UnstableApiUsage
                plugin.getLang().of("msg_config_reloaded").resolver(
                    Placeholder.unparsed("plugin", plugin.getName()),
                    Placeholder.unparsed("version", plugin.getPluginMeta().getVersion()),
                    Placeholder.unparsed("author", plugin.getPluginMeta().getAuthors().get(0))
                ).send(context.getSender());
            })
            .build();

        manager.register(List.of(reloadCommand));
    }
}
