package cc.mewcraft.mewfishing.command.command;

import cc.mewcraft.mewcore.cooldown.StackableCooldown;
import cc.mewcraft.mewfishing.MewFishing;
import cc.mewcraft.mewfishing.command.AbstractCommand;
import cc.mewcraft.mewfishing.command.CommandManager;
import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import me.lucko.helper.time.Time;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RestoreCommand extends AbstractCommand {
    public RestoreCommand(final MewFishing plugin, final CommandManager manager) {
        super(plugin, manager);
    }

    @Override public void register() {
        Command<CommandSender> restoreCommand = manager.commandBuilder("mewfish")
            .literal("restore")
            .argument(PlayerArgument.of("player"))
            .argument(IntegerArgument.of("amount"))
            .argument(EnumArgument.of(Unit.class, "unit"))
            .permission("mewfishing.command.restore")
            .handler(context -> {
                final Player player = context.get("player");
                final int amount = context.get("amount");
                final Unit unit = context.get("unit");
                CommandSender sender = context.getSender();

                final StackableCooldown cooldown = plugin.fishingPowerModule().getCooldownManager().getData(player.getUniqueId());

                switch (unit) {
                    case SECOND -> {
                        final long elapsed = cooldown.elapsed();
                        final long reduced = elapsed + TimeUnit.SECONDS.toMillis(amount);
                        cooldown.setLastTested(Time.nowMillis() - reduced);
                        plugin.lang()
                            .of("msg_restored_stacks_by_sec")
                            .replace("player", player.getName())
                            .replace("amount", amount)
                            .send(sender);
                    }
                    case POINT -> {
                        final long elapsed = cooldown.elapsed();
                        final long reduced = elapsed + amount * cooldown.getBaseTimeout();
                        cooldown.setLastTested(Time.nowMillis() - reduced);
                        plugin.lang().of("msg_restored_stacks_by_pts")
                            .replace("player", player.getName())
                            .replace("amount", amount)
                            .send(player);
                    }
                }
            }).build();

        manager.register(List.of(restoreCommand));
    }

    enum Unit {
        SECOND, POINT
    }

}
