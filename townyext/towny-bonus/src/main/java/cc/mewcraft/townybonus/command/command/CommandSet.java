package cc.mewcraft.townybonus.command.command;

import cc.mewcraft.townybonus.TownyBonus;
import cc.mewcraft.townybonus.command.AbstractCommand;
import cc.mewcraft.townybonus.command.CommandManager;
import cc.mewcraft.townybonus.command.argument.CultureArgument;
import cc.mewcraft.townybonus.command.argument.NationArgument;
import cc.mewcraft.townybonus.object.culture.Culture;
import cc.mewcraft.townybonus.util.UtilMetadata;
import cloud.commandframework.Command;
import com.palmergames.bukkit.towny.object.Nation;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandSet extends AbstractCommand {

    public CommandSet(final TownyBonus plugin, final CommandManager manager) {
        super(plugin, manager);
    }

    @Override public void register() {
        Command<CommandSender> setCommand = this.manager
            .commandBuilder("townybonus")
            .literal("set")
            .argument(NationArgument.of("nation"))
            .argument(CultureArgument.of("culture"))
            .handler(context -> {
                CommandSender sender = context.getSender();
                Nation nation = context.get("nation");
                Culture culture = context.get("culture");
                UtilMetadata.updateCulture(nation, culture.getName());
                TownyBonus.lang().info(sender, "cultureAddedToNation", "nation", nation.getName(), "culture", culture.getName());
            }).build();

        this.manager.register(List.of(setCommand));
    }

}
