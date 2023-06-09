package cc.mewcraft.adventurelevel.command.command;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import cc.mewcraft.adventurelevel.command.AbstractCommand;
import cc.mewcraft.adventurelevel.command.CommandManager;
import cc.mewcraft.adventurelevel.command.argument.PlayerDataArgument;
import cc.mewcraft.adventurelevel.data.PlayerData;
import cc.mewcraft.adventurelevel.level.category.LevelBean;
import cc.mewcraft.adventurelevel.level.category.LevelCategory;
import cloud.commandframework.Command;
import cloud.commandframework.arguments.flags.CommandFlag;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import me.lucko.helper.function.chain.Chain;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.function.Function;

public class ManageExpCommand extends AbstractCommand {

    private static final String NULL_PLAYER = "NULL";

    public ManageExpCommand(final AdventureLevelPlugin plugin, final CommandManager manager) {
        super(plugin, manager);
    }

    private enum LevelOption {
        MAIN(playerData -> playerData.getLevelBean(LevelCategory.PLAYER_DEATH)),
        PLAYER_DEATH(playerData -> playerData.getLevelBean(LevelCategory.PLAYER_DEATH)),
        ENTITY_DEATH(playerData -> playerData.getLevelBean(LevelCategory.ENTITY_DEATH)),
        FURNACE(playerData -> playerData.getLevelBean(LevelCategory.FURNACE)),
        BREED(playerData -> playerData.getLevelBean(LevelCategory.BREED)),
        VILLAGER_TRADE(playerData -> playerData.getLevelBean(LevelCategory.VILLAGER_TRADE)),
        FISHING(playerData -> playerData.getLevelBean(LevelCategory.FISHING)),
        BLOCK_BREAK(playerData -> playerData.getLevelBean(LevelCategory.BLOCK_BREAK)),
        EXP_BOTTLE(playerData -> playerData.getLevelBean(LevelCategory.EXP_BOTTLE)),
        GRINDSTONE(playerData -> playerData.getLevelBean(LevelCategory.GRINDSTONE));

        public final Function<PlayerData, LevelBean> mapping;

        LevelOption(Function<PlayerData, LevelBean> mapping) {
            this.mapping = mapping;
        }
    }

    @Override public void register() {
        Command<CommandSender> setExpCommand = manager.commandBuilder("adventurelevel")
            .literal("set")
            .argument(PlayerDataArgument.of("userdata"))
            .argument(EnumArgument.of(LevelOption.class, "category"))
            .argument(IntegerArgument.of("amount"))
            .flag(CommandFlag.builder("level").withAliases("l"))
            .permission("adventurelevel.command.admin")
            .handler(context -> {
                CommandSender sender = context.getSender();

                PlayerData userdata = context.get("userdata");
                LevelOption category = context.get("category");
                int amount = context.get("amount");

                boolean useLevel = context.flags().isPresent("level");
                if (useLevel)
                    category.mapping.apply(userdata).setLevel(amount);
                else
                    category.mapping.apply(userdata).setExperience(amount);

                plugin.getLang().of("msg_player_xp_is_set")
                    .resolver(
                        Placeholder.unparsed("player", Chain
                            .start(userdata.getUuid())
                            .map(Bukkit::getOfflinePlayer)
                            .map(OfflinePlayer::getName)
                            .end().orElse(NULL_PLAYER)),
                        Placeholder.unparsed("category", category.name()),
                        Placeholder.unparsed("amount", String.valueOf(amount))
                    ).send(sender);
            })
            .build();

        Command<CommandSender> addExpCommand = manager.commandBuilder("adventurelevel")
            .literal("add")
            .argument(PlayerDataArgument.of("userdata"))
            .argument(EnumArgument.of(LevelOption.class, "category"))
            .argument(IntegerArgument.of("amount"))
            .flag(CommandFlag.builder("level").withAliases("l"))
            .permission("adventurelevel.command.admin")
            .handler(context -> {
                CommandSender sender = context.getSender();

                PlayerData userdata = context.get("userdata");
                LevelOption category = context.get("category");
                int amount = context.get("amount");

                boolean useLevel = context.flags().isPresent("level");
                if (useLevel)
                    category.mapping.apply(userdata).addLevel(amount);
                else
                    category.mapping.apply(userdata).addExperience(amount);

                plugin.getLang().of("msg_player_xp_is_added")
                    .resolver(
                        Placeholder.unparsed("player", Chain
                            .start(userdata.getUuid())
                            .map(Bukkit::getOfflinePlayer)
                            .map(OfflinePlayer::getName)
                            .end().orElse(NULL_PLAYER)),
                        Placeholder.unparsed("category", category.name()),
                        Placeholder.unparsed("amount", String.valueOf(amount))
                    ).send(sender);
            })
            .build();

        manager.register(List.of(
            setExpCommand,
            addExpCommand
        ));
    }

}
