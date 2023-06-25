package cc.mewcraft.townyboard.command.command;

import cc.mewcraft.townyboard.TownyBonus;
import cc.mewcraft.townyboard.command.AbstractCommand;
import cc.mewcraft.townyboard.command.CommandManager;
import cloud.commandframework.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandReload extends AbstractCommand {

    public CommandReload(final TownyBonus plugin, final CommandManager manager) {
        super(plugin, manager);
    }

    @Override public void register() {
        Command<CommandSender> reloadCommand = this.manager
            .commandBuilder("townybonus")
            .permission("townybonus.admin")
            .literal("reload")
            .handler(context -> {
                TownyBonus.lang().info(context.getSender(), "reloadedConfig");
                TownyBonus.p.config.loadDefaultConfig();
                TownyBonus.p.reloadLang();
                TownyBonus.p.loadData();
            }).build();

        this.manager.register(List.of(reloadCommand));
    }
}
