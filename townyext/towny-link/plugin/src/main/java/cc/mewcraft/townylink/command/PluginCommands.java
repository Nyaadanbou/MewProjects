package cc.mewcraft.townylink.command;

import cc.mewcraft.mewcore.util.ServerOriginUtils;
import cc.mewcraft.townylink.TownyLinkPlugin;
import cc.mewcraft.townylink.api.TownyLink;
import cc.mewcraft.townylink.api.TownyLinkProvider;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Singleton
public class PluginCommands {
    private final TownyLinkPlugin plugin;
    private final CommandRegistry registry;

    @Inject
    public PluginCommands(TownyLinkPlugin plugin, CommandRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
    }

    public void registerCommands() {
        // Prepare commands

        registry.prepareCommand(registry
            .commandBuilder("home")
            .permission("townylink.command.home")
            .senderType(Player.class)
            .handler(ctx -> {
                Player sender = (Player) ctx.getSender();
                TownyLink link = TownyLinkProvider.get();
                String originId = ServerOriginUtils.getOriginId(sender.getUniqueId());
                link.requestPlayerTown(originId, sender.getUniqueId())
                    .thenAcceptSync(data -> {
                        if (data == null) {
                            sender.sendMessage("No home!");
                            return;
                        }

                        sender.sendMessage(Component.text("Home sweet home!"));
                    });
            }).build());

        // Register commands

        registry.registerCommands();
    }
}
