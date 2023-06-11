package cc.mewcraft.mewhelp.command;

import cc.mewcraft.mewhelp.MewHelp;
import cloud.commandframework.Command;
import cloud.commandframework.arguments.StaticArgument;
import cloud.commandframework.brigadier.CloudBrigadierManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.exceptions.ArgumentParseException;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import me.lucko.helper.terminable.Terminable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.util.ComponentMessageThrowable;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

public class CommandRegistry extends PaperCommandManager<CommandSender> implements Terminable {
    private static final Pattern SYNTAX_HIGHLIGHT_PATTERN = Pattern.compile("[^\\s\\w\\-]");

    private final List<Command<CommandSender>> preparedCommands;

    public CommandRegistry(MewHelp plugin) throws Exception {
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
            plugin.getLogger().info("Successfully registered Mojang Brigadier support for commands.");
        }

        // ---- Setup exception messages ----
        new MinecraftExceptionHandler<CommandSender>()
            .withDefaultHandlers()
            .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SYNTAX, e -> {
                final InvalidSyntaxException exception = (InvalidSyntaxException) e;
                final Component correctSyntaxMessage = Component
                    .text("/%s".formatted(exception.getCorrectSyntax()))
                    .color(NamedTextColor.GRAY)
                    .replaceText(config -> {
                        config.match(SYNTAX_HIGHLIGHT_PATTERN);
                        config.replacement(builder -> builder.color(NamedTextColor.WHITE));
                    });
                return plugin.getLanguages()
                    .of("err_invalid_syntax")
                    .resolver(Placeholder.component("syntax", correctSyntaxMessage))
                    .component();
            })
            .withHandler(MinecraftExceptionHandler.ExceptionType.ARGUMENT_PARSING, e -> {
                final ArgumentParseException exception = (ArgumentParseException) e;
                return plugin.getLanguages()
                    .of("err_argument_parsing")
                    .resolver(Placeholder.component("args", getMessage(exception.getCause())))
                    .component();
            })
            .apply(this, AudienceProvider.nativeAudience());
    }

    public final void prepareCommand(final Command<CommandSender> command) {
        this.preparedCommands.add(command);
    }

    public final void registerCommands() {
        for (final Command<CommandSender> added : this.preparedCommands) command(added);
    }

    @Override public void close() {
        commandRegistrationHandler().unregisterRootCommand(StaticArgument.of("help"));
    }

    private static Component getMessage(final Throwable throwable) {
        final Component msg = ComponentMessageThrowable.getOrConvertMessage(throwable);
        return msg == null ? Component.empty() : msg;
    }
}
