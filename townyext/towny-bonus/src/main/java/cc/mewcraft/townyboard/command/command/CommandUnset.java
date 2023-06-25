package cc.mewcraft.townyboard.command.command;

import cc.mewcraft.townyboard.TownyBonus;
import cc.mewcraft.townyboard.command.AbstractCommand;
import cc.mewcraft.townyboard.command.CommandManager;
import cc.mewcraft.townyboard.command.argument.NationArgument;
import cc.mewcraft.townyboard.util.UtilMetadata;
import cloud.commandframework.Command;
import com.palmergames.bukkit.towny.object.Nation;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandUnset extends AbstractCommand {

    public CommandUnset(final TownyBonus plugin, final CommandManager manager) {
        super(plugin, manager);
    }

    @Override public void register() {
        Command<CommandSender> unsetCommand = this.manager
            .commandBuilder("townybonus")
            .literal("unset")
            .argument(NationArgument.of("nation"))
            .handler(context -> {
                CommandSender sender = context.getSender();
                Nation nation = context.get("nation");
                UtilMetadata.removeCulture(nation);
                TownyBonus.lang().info(sender, "cultureRemovedFromNation", "nation", nation.getName());
            }).build();

        this.manager.register(List.of(unsetCommand));
    }

}
