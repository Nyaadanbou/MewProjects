package cc.mewcraft.enchantment.gui.command;

import cc.mewcraft.enchantment.gui.EnchantGuiPlugin;
import cc.mewcraft.enchantment.gui.gui.EnchantMenu;
import cc.mewcraft.mewcore.command.SimpleCommands;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.entity.Player;

@Singleton
public class PluginCommands extends SimpleCommands<EnchantGuiPlugin> {
    @Inject
    public PluginCommands(final EnchantGuiPlugin plugin) throws Exception {
        super(plugin);
    }

    @Override public void prepareAndRegister() {
        // Prepare commands
        registry.prepareCommand(registry
            .commandBuilder("enchantgui")
            .literal("open")
            .permission("enchantgui.command.open")
            .argument(PlayerArgument.optional("target"))
            .handler(ctx -> {
                Player viewer;
                if (ctx.contains("target")) {
                    viewer = ctx.get("target");
                } else if (ctx.getSender() instanceof Player player) {
                    viewer = player;
                } else return;
                plugin.getInjector().getInstance(EnchantMenu.class).open(viewer);
            }).build());
        registry.prepareCommand(registry
            .commandBuilder("enchantgui")
            .literal("reload")
            .permission("enchantgui.command.reload")
            .handler(ctx -> {
                plugin.onDisable();
                plugin.onEnable();
            }).build());

        // Register commands
        registry.registerCommands();
    }
}
