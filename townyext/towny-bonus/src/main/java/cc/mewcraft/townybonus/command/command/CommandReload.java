package cc.mewcraft.townybonus.command.command;

import cc.mewcraft.townybonus.TownyBonus;
import cc.mewcraft.townybonus.command.AbstractCommand;
import cc.mewcraft.townybonus.command.CommandManager;
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
