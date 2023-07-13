package cc.mewcraft.townybonus.command;

import cc.mewcraft.townybonus.TownyBonus;
import cc.mewcraft.townybonus.command.command.CommandReload;
import cc.mewcraft.townybonus.command.command.CommandSet;
import cc.mewcraft.townybonus.command.command.CommandUnset;
import cloud.commandframework.Command;
import cloud.commandframework.arguments.flags.CommandFlag;
import cloud.commandframework.brigadier.CloudBrigadierManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class CommandManager extends PaperCommandManager<CommandSender> {

    public static final CloudKey<TownyBonus> PLUGIN = SimpleCloudKey.of("mewfishing:plugin", TypeToken.get(TownyBonus.class));
    private static final Component NULL = Component.text("null");
    private final Map<String, CommandFlag.Builder<?>> flagRegistry = new HashMap<>();

    public CommandManager(TownyBonus plugin) throws Exception {
        super(
            plugin,
            AsynchronousCommandExecutionCoordinator.<CommandSender>builder().build(),
            Function.identity(),
            Function.identity()
        );

        // ---- Register Brigadier ----
        if (this.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            this.registerBrigadier();
            final @Nullable CloudBrigadierManager<CommandSender, ?> brigManager = this.brigadierManager();
            if (brigManager != null) {
                brigManager.setNativeNumberSuggestions(false);
            }
            plugin.getLogger().info("Successfully registered Mojang Brigadier support for commands.");
        }

        // ---- Register Asynchronous Completion Listener ----
        if (this.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            this.registerAsynchronousCompletions();
            plugin.getLogger().info("Successfully registered asynchronous command completion listener.");
        }

        // ---- Inject instances into the command context ----
        this.registerCommandPreProcessor(ctx -> ctx.getCommandContext().store(PLUGIN, plugin));

        // ---- Change default exception messages ----
        new MinecraftExceptionHandler<CommandSender>()
            .withDefaultHandlers()
            .apply(this, sender -> AudienceProvider.nativeAudience().apply(sender));

        // ---- Register all commands ----
        Stream.of(
            new CommandSet(plugin, this),
            new CommandUnset(plugin, this),
            new CommandReload(plugin, this)
        ).forEach(AbstractCommand::register);
    }

    public CommandFlag.Builder<?> getFlag(final String name) {
        return flagRegistry.get(name);
    }

    public void registerFlag(final String name, final CommandFlag.Builder<?> flagBuilder) {
        flagRegistry.put(name, flagBuilder);
    }

    public void register(final List<Command<CommandSender>> commands) {
        commands.forEach(this::command);
    }

}
