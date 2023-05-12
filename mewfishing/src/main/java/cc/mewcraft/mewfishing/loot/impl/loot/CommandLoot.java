package cc.mewcraft.mewfishing.loot.impl.loot;

import cc.mewcraft.mewfishing.event.FishLootEvent;
import cc.mewcraft.mewfishing.loot.api.Conditioned;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

@DefaultQualifier(NonNull.class)
public class CommandLoot extends AbstractLoot<CommandLoot.Commands> {

    private final Commands commands;

    public CommandLoot(
        final double weight,
        final String amount,
        final List<Conditioned> conditions,
        final List<String> commands
    ) {
        super(weight, amount, conditions);
        this.commands = new Commands(commands);
    }

    @Override public void apply(final FishLootEvent event) {
        commands.execute(event.getPlayer());
    }

    static class Commands {
        private final List<String> commands;

        public Commands(final List<String> commands) {
            this.commands = commands;
        }

        public void execute(CommandSender sender) {
            commands.forEach(cmd -> {
                cmd = cmd.replace("%player_name%", sender.getName());
                if (cmd.startsWith("@ ")) {
                    // player sudo command
                    Bukkit.dispatchCommand(sender, cmd.substring(2));
                } else if (cmd.startsWith("+ ")) {
                    // player sudo chat
                    if (sender instanceof Player player)
                        player.chat(cmd.substring(2));
                } else {
                    // console command
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                }
            });
        }
    }

}
