package cc.mewcraft.mewfishing.command.command;

import cc.mewcraft.mewfishing.MewFishing;
import cc.mewcraft.mewfishing.command.AbstractCommand;
import cc.mewcraft.mewfishing.command.CommandManager;
import cloud.commandframework.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand extends AbstractCommand {
    public ReloadCommand(final MewFishing plugin, final CommandManager manager) {
        super(plugin, manager);
    }

    @Override public void register() {
        Command<CommandSender> reloadCommand = manager.commandBuilder("mewfish")
            .literal("reload")
            .permission("mewfishing.command.reload")
            .handler(context -> {
                MewFishing.instance().onDisable();
                MewFishing.instance().onEnable();
                MewFishing.translations().of("reloadedConfig").send(context.getSender());
            })
            .build();
        manager.register(List.of(reloadCommand));
    }
}
