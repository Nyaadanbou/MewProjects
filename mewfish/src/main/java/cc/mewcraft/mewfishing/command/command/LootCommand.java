package cc.mewcraft.mewfishing.command.command;

import cc.mewcraft.mewfishing.MewFish;
import cc.mewcraft.mewfishing.command.AbstractCommand;
import cc.mewcraft.mewfishing.command.CommandManager;
import cc.mewcraft.nms.MewNMSProvider;
import cloud.commandframework.Command;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LootCommand extends AbstractCommand {
    public LootCommand(final MewFish plugin, final CommandManager manager) {
        super(plugin, manager);
    }

    @Override public void register() {
        Command<CommandSender> lootCommand = manager.commandBuilder("mewfish")
            .literal("loots")
            .permission("mewfishing.command.loot")
            .senderType(Player.class)
            .handler(context -> {
                Player sender = (Player) context.getSender();
                Location location = sender.getLocation();
                String biome = MewNMSProvider.get().biomeKey(location).asString();
                sender.sendMessage(biome);
            }).build();

        manager.register(List.of(lootCommand));
    }

}
