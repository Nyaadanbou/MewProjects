package cc.mewcraft.mewfishing.command.command;

import cc.mewcraft.mewfishing.MewFishing;
import cc.mewcraft.mewfishing.command.AbstractCommand;
import cc.mewcraft.mewfishing.command.CommandManager;
import cc.mewcraft.mewfishing.nms.player.PlayerAction;
import cloud.commandframework.Command;
import cloud.commandframework.bukkit.arguments.selector.MultiplePlayerSelector;
import cloud.commandframework.bukkit.parsers.selector.MultiplePlayerSelectorArgument;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ClickCommand extends AbstractCommand {
    public ClickCommand(final MewFishing plugin, final CommandManager manager) {
        super(plugin, manager);
    }

    @Override public void register() {
        Command<CommandSender> checkCommand = manager.commandBuilder("mewfish")
            .literal("click")
            .argument(MultiplePlayerSelectorArgument.of("player"))
            .permission("mewfishing.command.click")
            .handler(context -> {
                CommandSender sender = context.getSender();
                MultiplePlayerSelector player = context.get("player");
                player.getPlayers().forEach(PlayerAction::doRightClick);
                sender.sendMessage("Done!");
            }).build();

        manager.register(List.of(checkCommand));
    }

}
