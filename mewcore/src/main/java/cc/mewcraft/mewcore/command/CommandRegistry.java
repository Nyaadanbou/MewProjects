package cc.mewcraft.mewcore.command;

import cloud.commandframework.Command;
import cloud.commandframework.brigadier.CloudBrigadierManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CommandRegistry<P extends Plugin> extends PaperCommandManager<CommandSender> {
    private final List<Command<CommandSender>> preparedCommands;

    public CommandRegistry(@NotNull P plugin) throws Exception {
        super(
            plugin,
            CommandExecutionCoordinator.simpleCoordinator(),
            Function.identity(),
            Function.identity()
        );

        this.preparedCommands = new ArrayList<>();

        // ---- Register Brigadier ----
        if (hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            registerBrigadier();
            CloudBrigadierManager<CommandSender, ?> brigManager = brigadierManager();
            if (brigManager != null) {
                brigManager.setNativeNumberSuggestions(false);
            }
            plugin.getLogger().info("Successfully registered Mojang Brigadier support for commands");
        }

        // ---- Setup exception messages ----
        new MinecraftExceptionHandler<CommandSender>()
            .withDefaultHandlers()
            .apply(this, sender -> AudienceProvider.nativeAudience().apply(sender));
    }

    /**
     * Prepares given command.
     * <p>
     * Prepared commands will become effective after {@link #registerCommands()} is called.
     *
     * @param command the command to be prepared
     */
    public final void prepareCommand(final @NotNull Command<CommandSender> command) {
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
