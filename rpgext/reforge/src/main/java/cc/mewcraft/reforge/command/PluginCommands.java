package cc.mewcraft.reforge.command;

import cc.mewcraft.reforge.ReforgePlugin;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class PluginCommands {
    private final Injector injector;
    private final ReforgePlugin plugin;
    private final CommandRegistry registry;

    @Inject
    public PluginCommands(
        Injector injector,
        ReforgePlugin plugin,
        CommandRegistry registry
    ) {
        this.injector = injector;
        this.plugin = plugin;
        this.registry = registry;
    }

    public void registerCommands() {
        // Prepare commands
        registry.prepareCommand(registry
            .commandBuilder("reforge")
            .literal("reload")
            .permission("reforge.command.reload")
            .handler(ctx -> {
                plugin.reloadConfig();
            }).build());

        // Register commands
        registry.registerCommands();
    }
}
