package cc.mewcraft.mewfishing.command.command;

import cc.mewcraft.mewcore.cooldown.ChargeBasedCooldown;
import cc.mewcraft.mewcore.cooldown.ChargeBasedCooldownMap;
import cc.mewcraft.mewfishing.MewFishing;
import cc.mewcraft.mewfishing.command.AbstractCommand;
import cc.mewcraft.mewfishing.command.CommandManager;
import cloud.commandframework.Command;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CheckCommand extends AbstractCommand {

    public CheckCommand(final MewFishing plugin, final CommandManager manager) {
        super(plugin, manager);
    }

    @Override public void register() {
        Command<CommandSender> checkCommand = manager.commandBuilder("mewfish")
            .literal("check")
            .argument(PlayerArgument.of("player"))
            .permission("mewfishing.command.check")
            .handler(context -> {
                CommandSender sender = context.getSender();
                final Player player = context.get("player");
                final ChargeBasedCooldownMap<UUID> fishingPowerMap = plugin.fishingPowerModule().getFishingPowerMap();
                final ChargeBasedCooldown fishingPower = fishingPowerMap.get(player.getUniqueId());
                plugin.lang().of("msg_check_fishing_power")
                    .replace("player", player.getName())
                    .replace("charges", fishingPower.getAvailable())
                    .replace("remaining_now", fishingPower.remainingTime(TimeUnit.SECONDS))
                    .replace("remaining_all", fishingPower.remainingTimeFull(TimeUnit.SECONDS))
                    .send(sender);
            }).build();

        manager.register(List.of(checkCommand));
    }

}
