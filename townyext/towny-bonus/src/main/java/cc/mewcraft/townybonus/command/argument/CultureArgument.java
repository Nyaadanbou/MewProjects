package cc.mewcraft.townybonus.command.argument;

import cc.mewcraft.townybonus.TownyBonus;
import cc.mewcraft.townybonus.object.culture.Culture;
import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

public class CultureArgument extends CommandArgument<CommandSender, Culture> {

    public CultureArgument(
        boolean required,
        String name,
        String defaultValue,
        @Nullable BiFunction<CommandContext<CommandSender>, String, List<String>> suggestionsProvider,
        ArgumentDescription defaultDescription
    ) {
        super(required, name, new CultureArgument.Parser(), defaultValue, Culture.class, suggestionsProvider, defaultDescription);
    }

    public static CultureArgument of(final String name) {
        return builder(name).build();
    }

    public static CultureArgument optional(final String name) {
        return builder(name).asOptional().build();
    }

    public static CultureArgument.Builder builder(final String name) {
        return new CultureArgument.Builder(name);
    }

    public static final class Parser implements ArgumentParser<CommandSender, Culture> {
        @Override
        public @NotNull ArgumentParseResult<Culture> parse(
            final @NotNull CommandContext<CommandSender> commandContext,
            final Queue<String> inputQueue
        ) {
            String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(CultureArgument.Parser.class, commandContext));
            }

            Culture target = TownyBonus.p.cultureManager.getOrNull(input);
            if (target != null) {
                inputQueue.remove();
                return ArgumentParseResult.success(target);
            }

            return ArgumentParseResult.failure(new IllegalArgumentException("Culture not found: " + input));
        }

        @Override
        public @NotNull List<String> suggestions(
            final @NotNull CommandContext<CommandSender> commandContext,
            final @NotNull String input
        ) {
            return TownyBonus.p.cultureManager.getCultureStartingWith(input);
        }
    }

    public static final class Builder extends CommandArgument.TypedBuilder<CommandSender, Culture, CultureArgument.Builder> {
        private Builder(final String name) {
            super(Culture.class, name);
        }

        @Override
        public @NotNull CultureArgument build() {
            return new CultureArgument(
                this.isRequired(),
                this.getName(),
                this.getDefaultValue(),
                this.getSuggestionsProvider(),
                this.getDefaultDescription()
            );
        }
    }

}
