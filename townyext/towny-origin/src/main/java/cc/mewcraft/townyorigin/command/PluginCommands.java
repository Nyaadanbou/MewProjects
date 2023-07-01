package cc.mewcraft.townyorigin.command;

import cc.mewcraft.mewcore.command.SimpleCommands;
import cc.mewcraft.townyorigin.TownyOrigin;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PluginCommands extends SimpleCommands<TownyOrigin> {
    @Inject
    public PluginCommands(final TownyOrigin plugin) throws Exception {
        super(plugin);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override public void prepareAndRegister() {
        // Prepare commands
        registry.prepareCommand(registry
            .commandBuilder("townyorigin")
            .literal("reload")
            .permission("townyorigin.command.reload")
            .handler(ctx -> {
                plugin.onDisable();
                plugin.onEnable();
                plugin.getLang().of("msg_plugin_reloaded")
                    .replace("plugin", plugin.getPluginMeta().getName())
                    .replace("version", plugin.getPluginMeta().getVersion())
                    .replace("author", plugin.getPluginMeta().getAuthors().get(0))
                    .send(ctx.getSender());
            }).build());

        // Register commands
        registry.registerCommands();
    }
}
