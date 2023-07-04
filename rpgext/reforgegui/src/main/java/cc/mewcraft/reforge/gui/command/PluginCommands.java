package cc.mewcraft.reforge.gui.command;

import cc.mewcraft.reforge.gui.ReforgePlugin;
import cc.mewcraft.reforge.gui.object.ReforgeWindowWrapper;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.entity.Player;

@Singleton
public class PluginCommands {
    private final ReforgePlugin plugin;
    private final CommandRegistry registry;

    @Inject
    public PluginCommands(
        ReforgePlugin plugin,
        CommandRegistry registry
    ) {
        this.plugin = plugin;
        this.registry = registry;
    }

    public void registerCommands() {
        // Prepare commands

        registry.prepareCommand(registry
            .commandBuilder("reforgegui")
            .literal("open")
            .argument(PlayerArgument.optional("target"))
            .permission("reforgegui.command.open")
            .handler(ctx -> {
                ReforgeWindowWrapper window = plugin.getInjector().getInstance(ReforgeWindowWrapper.class);
                if (ctx.contains("target")) {
                    Player target = ctx.get("target");
                    window.open(target);
                } else if (ctx.getSender() instanceof Player player) {
                    window.open(player);
                }
            }).build());
        registry.prepareCommand(registry
            .commandBuilder("reforgegui")
            .literal("reload")
            .permission("reforgegui.command.reload")
            .handler(ctx -> {
                plugin.onDisable();
                plugin.onEnable();
            }).build());

        // Register commands
        registry.registerCommands();
    }
}
