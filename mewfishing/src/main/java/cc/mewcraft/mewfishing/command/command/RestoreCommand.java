package cc.mewcraft.mewfishing.command.command;

import cc.mewcraft.mewcore.cooldown.ChargeBasedCooldown;
import cc.mewcraft.mewcore.cooldown.ChargeBasedCooldownMap;
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
import java.util.UUID;
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

                final ChargeBasedCooldownMap<UUID> fishingPowerMap = MewFishing.instance().getFishPowerModule().getFishingPowerMap();
                final ChargeBasedCooldown fishingPower = fishingPowerMap.get(player.getUniqueId());

                switch (unit) {
                    case SECOND -> {
                        final long elapsed = fishingPower.elapsed();
                        final long reduced = elapsed + TimeUnit.SECONDS.toMillis(amount);
                        fishingPower.setLastTested(Time.nowMillis() - reduced);
                        MewFishing.translations()
                            .of("restoreSeconds")
                            .replace("player", player.getName())
                            .replace("amount", amount)
                            .send(sender);
                    }
                    case POINT -> {
                        final long elapsed = fishingPower.elapsed();
                        final long reduced = elapsed + amount * fishingPower.getBaseTimeout();
                        fishingPower.setLastTested(Time.nowMillis() - reduced);
                        MewFishing.translations().of("restoreCharge")
                            .replace("player", player.getName())
                            .replace("amount", amount)
                            .send(player);
                    }
                }
            })
            .build();
        manager.register(List.of(restoreCommand));
    }

    enum Unit {
        SECOND, POINT
    }
}
