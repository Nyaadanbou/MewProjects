package cc.mewcraft.adventurelevel.command;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import cc.mewcraft.adventurelevel.command.command.ManageExpCommand;
import cc.mewcraft.adventurelevel.command.command.ReloadPluginCommand;
import cloud.commandframework.Command;
import cloud.commandframework.brigadier.CloudBrigadierManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class CommandManager extends PaperCommandManager<CommandSender> {

    public CommandManager(AdventureLevelPlugin plugin) throws Exception {
        super(
            plugin,
            CommandExecutionCoordinator.simpleCoordinator(), // use sync, so what we see in code is what it actually does
            Function.identity(),
            Function.identity()
        );

        // ---- Register Brigadier ----
        if (hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            registerBrigadier();
            final @Nullable CloudBrigadierManager<CommandSender, ?> brigManager = brigadierManager();
            if (brigManager != null) {
                brigManager.setNativeNumberSuggestions(false);
            }
            plugin.getSLF4JLogger().info("Successfully registered Mojang Brigadier support for commands.");
        }

        // ---- Register Asynchronous Completion Listener ----
        if (hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            registerAsynchronousCompletions();
            plugin.getSLF4JLogger().info("Successfully registered asynchronous command completion listener.");
        }

        // ---- Change default exception messages ----
        new MinecraftExceptionHandler<CommandSender>()
            .withDefaultHandlers()
            .apply(this, sender -> AudienceProvider.nativeAudience().apply(sender));

        // ---- Register all commands ----
        Stream.of(
            new ReloadPluginCommand(plugin, this),
            new ManageExpCommand(plugin, this)
        ).forEach(AbstractCommand::register);
    }

    public void register(final List<Command<CommandSender>> commands) {
        commands.forEach(this::command);
    }

}
