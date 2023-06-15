package cc.mewcraft.townybonus.command.argument;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

public class TownArgument extends CommandArgument<CommandSender, Town> {

    public TownArgument(
        boolean required,
        String name,
        String defaultValue,
        @Nullable BiFunction<CommandContext<CommandSender>, String, List<String>> suggestionsProvider,
        ArgumentDescription defaultDescription
    ) {
        super(required, name, new Parser(), defaultValue, Town.class, suggestionsProvider, defaultDescription);
    }

    public static TownArgument of(final String name) {
        return builder(name).build();
    }

    public static TownArgument optional(final String name) {
        return builder(name).asOptional().build();
    }

    public static TownArgument.Builder builder(final String name) {
        return new TownArgument.Builder(name);
    }

    public static final class Parser implements ArgumentParser<CommandSender, Town> {
        @Override
        public @NotNull ArgumentParseResult<Town> parse(
            final @NotNull CommandContext<CommandSender> commandContext,
            final Queue<String> inputQueue
        ) {
            String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(TownArgument.Parser.class, commandContext));
            }

            Town target = TownyAPI.getInstance().getTown(input);
            if (target != null) {
                inputQueue.remove();
                return ArgumentParseResult.success(target);
            }

            return ArgumentParseResult.failure(new IllegalArgumentException("Town not found: " + input));
        }

        @Override
        public @NotNull List<String> suggestions(
            final @NotNull CommandContext<CommandSender> commandContext,
            final @NotNull String input
        ) {
            return TownyAPI.getTownyObjectStartingWith(input, "t");
        }
    }

    public static final class Builder extends CommandArgument.TypedBuilder<CommandSender, Town, TownArgument.Builder> {
        private Builder(final String name) {
            super(Town.class, name);
        }

        @Override
        public @NotNull TownArgument build() {
            return new TownArgument(
                this.isRequired(),
                this.getName(),
                this.getDefaultValue(),
                this.getSuggestionsProvider(),
                this.getDefaultDescription()
            );
        }
    }

}