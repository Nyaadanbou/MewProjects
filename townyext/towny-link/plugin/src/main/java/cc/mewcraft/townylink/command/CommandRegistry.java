package cc.mewcraft.townylink.command;

import cc.mewcraft.townylink.TownyLinkPlugin;
import cloud.commandframework.Command;
import cloud.commandframework.arguments.flags.CommandFlag;
import cloud.commandframework.brigadier.CloudBrigadierManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Singleton
public class CommandRegistry extends PaperCommandManager<CommandSender> {

    private final Map<String, CommandFlag.Builder<?>> flagRegistry;
    private final List<Command<CommandSender>> preparedCommands;

    @Inject
    public CommandRegistry(TownyLinkPlugin plugin) throws Exception {
        super(
            plugin,
            CommandExecutionCoordinator.simpleCoordinator(),
            Function.identity(),
            Function.identity()
        );

        this.flagRegistry = new HashMap<>();
        this.preparedCommands = new ArrayList<>();

        // ---- Register Brigadier ----
        if (hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            registerBrigadier();
            CloudBrigadierManager<CommandSender, ?> brigManager = brigadierManager();
            if (brigManager != null) {
                brigManager.setNativeNumberSuggestions(false);
            }
            plugin.getLogger().info("Successfully registered Mojang Brigadier support for commands.");
        }

        // ---- Setup exception messages ----
        new MinecraftExceptionHandler<CommandSender>()
            .withDefaultHandlers()
            .apply(this, sender -> AudienceProvider.nativeAudience().apply(sender));
    }

    public CommandFlag.Builder<?> getFlag(final String name) {
        return this.flagRegistry.get(name);
    }

    public void registerFlag(final String name, final CommandFlag.Builder<?> flagBuilder) {
        this.flagRegistry.put(name, flagBuilder);
    }

    public final void prepareCommand(final Command<CommandSender> command) {
        this.preparedCommands.add(command);
    }

    /**
     * Registers all added commands.
     * <p>
     * This method will make the prepared commands effective.
     */
    public final void registerCommands() {
        for (final Command<CommandSender> added : this.preparedCommands) {
            command(added);
        }
    }

}
