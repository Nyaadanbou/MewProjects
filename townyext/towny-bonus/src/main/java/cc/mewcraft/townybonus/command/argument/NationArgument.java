package cc.mewcraft.townybonus.command.argument;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

public class NationArgument extends CommandArgument<CommandSender, Nation> {

    public NationArgument(
        boolean required,
        String name,
        String defaultValue,
        @Nullable BiFunction<CommandContext<CommandSender>, String, List<String>> suggestionsProvider,
        ArgumentDescription defaultDescription
    ) {
        super(required, name, new NationArgument.Parser(), defaultValue, Nation.class, suggestionsProvider, defaultDescription);
    }

    public static NationArgument of(final String name) {
        return builder(name).build();
    }

    public static NationArgument optional(final String name) {
        return builder(name).asOptional().build();
    }

    public static NationArgument.Builder builder(final String name) {
        return new NationArgument.Builder(name);
    }

    public static final class Parser implements ArgumentParser<CommandSender, Nation> {
        @Override
        public @NotNull ArgumentParseResult<Nation> parse(
            final @NotNull CommandContext<CommandSender> commandContext,
            final Queue<String> inputQueue
        ) {
            String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(NationArgument.Parser.class, commandContext));
            }

            Nation target = TownyAPI.getInstance().getNation(input);
            if (target != null) {
                inputQueue.remove();
                return ArgumentParseResult.success(target);
            }

            return ArgumentParseResult.failure(new IllegalArgumentException("Nation not found: " + input));
        }

        @Override
        public @NotNull List<String> suggestions(
            final @NotNull CommandContext<CommandSender> commandContext,
            final @NotNull String input
        ) {
            return TownyAPI.getTownyObjectStartingWith(input, "n");
        }
    }

    public static final class Builder extends CommandArgument.TypedBuilder<CommandSender, Nation, NationArgument.Builder> {
        private Builder(final String name) {
            super(Nation.class, name);
        }

        @Override
        public @NotNull NationArgument build() {
            return new NationArgument(
                this.isRequired(),
                this.getName(),
                this.getDefaultValue(),
                this.getSuggestionsProvider(),
                this.getDefaultDescription()
            );
        }
    }

}
