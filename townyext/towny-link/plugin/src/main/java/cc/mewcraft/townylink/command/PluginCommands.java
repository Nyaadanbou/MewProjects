package cc.mewcraft.townylink.command;

import cc.mewcraft.mewcore.util.ServerOriginUtils;
import cc.mewcraft.townylink.TownyLinkPlugin;
import cc.mewcraft.townylink.api.TownyLink;
import cc.mewcraft.townylink.api.TownyLinkProvider;
import cc.mewcraft.townylink.teleport.NetworkTeleport;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Singleton
public class PluginCommands {
    private final TownyLinkPlugin plugin;
    private final CommandRegistry registry;
    private final NetworkTeleport teleport;

    @Inject
    public PluginCommands(
        TownyLinkPlugin plugin,
        CommandRegistry registry,
        NetworkTeleport teleport
    ) {
        this.plugin = plugin;
        this.registry = registry;
        this.teleport = teleport;
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

                if (originId == null) {
                    plugin.getLang().of("msg_no_home_for_no_origin").send(sender);
                    return;
                }

                link.requestPlayerTown(originId, sender.getUniqueId())
                    .exceptionallySync(t -> null)
                    .thenAcceptSync(data -> {
                        if (data == null) {
                            plugin.getLang().of("msg_no_home_for_no_town").send(sender);
                            return;
                        }

                        teleport.teleportPlayer(sender, data.point(), originId);
                        plugin.getLang().of("msg_arrived_home").send(sender);
                    });
            }).build());

        registry.prepareCommand(registry
            .commandBuilder("townylink")
            .literal("reload")
            .permission("townylink.command.reload")
            .handler(ctx -> {
                CommandSender sender = ctx.getSender();
                plugin.onDisable();
                plugin.onEnable();

                //noinspection UnstableApiUsage
                plugin.getLang().of("msg_plugin_reloaded")
                    .replace("plugin", plugin.getPluginMeta().getName())
                    .replace("version", plugin.getPluginMeta().getVersion())
                    .replace("author", plugin.getPluginMeta().getAuthors().get(0))
                    .send(sender);

            }).build());

        // Register commands

        registry.registerCommands();
    }
}
