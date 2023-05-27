package cc.mewcraft.adventurelevel.command.argument;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import cc.mewcraft.adventurelevel.data.PlayerData;
import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.bukkit.BukkitCommandContextKeys;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import me.lucko.helper.utils.annotation.NonnullByDefault;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

@NonnullByDefault
public class PlayerDataArgument extends CommandArgument<CommandSender, PlayerData> {

    public PlayerDataArgument(boolean required,
        String name,
        String defaultValue,
        @Nullable BiFunction<CommandContext<CommandSender>, String, List<String>> suggestionsProvider,
        ArgumentDescription defaultDescription) {
        super(required, name, new Parser(), defaultValue, PlayerData.class, suggestionsProvider, defaultDescription);
    }

    public static PlayerDataArgument of(final String name) {
        return builder(name).build();
    }

    public static PlayerDataArgument optional(final String name) {
        return builder(name).asOptional().build();
    }

    public static Builder builder(final String name) {
        return new Builder(name);
    }

    public static final class Parser implements ArgumentParser<CommandSender, PlayerData> {
        @Override
        public ArgumentParseResult<PlayerData> parse(
            final CommandContext<CommandSender> commandContext,
            final Queue<String> inputQueue
        ) {
            String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(Parser.class, commandContext));
            }

            CommandSender sender = commandContext.getSender();
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(input);
            if (offlinePlayer == null) {
                return ArgumentParseResult.failure(
                    new IllegalArgumentException(AdventureLevelPlugin.getInstance().getLang().of("msg_player_is_null").locale(sender).plain())
                );
            }

            try {
                PlayerData playerData = AdventureLevelPlugin.getInstance().getPlayerDataManager().load(offlinePlayer).get();
                if (!playerData.equals(PlayerData.DUMMY)) {
                    inputQueue.remove();
                    return ArgumentParseResult.success(playerData);
                }
            } catch (InterruptedException | ExecutionException e) {
                return ArgumentParseResult.failure(
                    new IllegalArgumentException(AdventureLevelPlugin.getInstance().getLang().of("msg_player_is_null").plain())
                );
            }

            return ArgumentParseResult.failure(
                new IllegalArgumentException(AdventureLevelPlugin.getInstance().getLang().of("msg_player_is_null").plain())
            );
        }

        @Override
        public List<String> suggestions(
            final CommandContext<CommandSender> commandContext,
            final String input
        ) {
            List<String> suggestions = new ArrayList<>();

            for (Player player : Bukkit.getOnlinePlayers()) {
                final CommandSender bukkit = commandContext.get(BukkitCommandContextKeys.BUKKIT_COMMAND_SENDER);
                if (bukkit instanceof Player && !((Player) bukkit).canSee(player)) {
                    continue;
                }
                suggestions.add(player.getName());
            }

            return suggestions;
        }
    }

    public static final class Builder extends TypedBuilder<CommandSender, PlayerData, Builder> {
        private Builder(final String name) {
            super(PlayerData.class, name);
        }

        @Override
        public PlayerDataArgument build() {
            return new PlayerDataArgument(
                this.isRequired(),
                this.getName(),
                this.getDefaultValue(),
                this.getSuggestionsProvider(),
                this.getDefaultDescription()
            );
        }
    }

}

