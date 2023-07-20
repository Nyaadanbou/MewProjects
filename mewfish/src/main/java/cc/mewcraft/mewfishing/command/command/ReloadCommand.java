package cc.mewcraft.mewfishing.command.command;

import cc.mewcraft.mewfishing.MewFish;
import cc.mewcraft.mewfishing.command.AbstractCommand;
import cc.mewcraft.mewfishing.command.CommandManager;
import cloud.commandframework.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand extends AbstractCommand {
    public ReloadCommand(final MewFish plugin, final CommandManager manager) {
        super(plugin, manager);
    }

    @Override public void register() {
        Command<CommandSender> reloadCommand = manager.commandBuilder("mewfish")
            .literal("reload")
            .permission("mewfishing.command.reload")
            .handler(context -> {
                plugin.onDisable();
                plugin.onEnable();
                plugin.lang().of("msg_config_reloaded").send(context.getSender());
            }).build();

        manager.register(List.of(reloadCommand));
    }

}
