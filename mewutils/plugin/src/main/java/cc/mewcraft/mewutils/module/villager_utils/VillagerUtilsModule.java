package cc.mewcraft.mewutils.module.villager_utils;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.bukkit.arguments.selector.MultipleEntitySelector;
import cloud.commandframework.bukkit.arguments.selector.SingleEntitySelector;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.bukkit.parsers.selector.MultipleEntitySelectorArgument;
import cloud.commandframework.bukkit.parsers.selector.SingleEntitySelectorArgument;
import cc.mewcraft.mewutils.api.MewPlugin;
import cc.mewcraft.mewutils.api.command.CommandRegistry;
import cc.mewcraft.mewutils.api.module.ModuleBase;
import com.destroystokyo.paper.entity.villager.Reputation;
import com.destroystokyo.paper.entity.villager.ReputationType;
import com.google.inject.Inject;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.MerchantRecipe;

import java.util.HashMap;
import java.util.Optional;

public class VillagerUtilsModule extends ModuleBase {

    @Inject
    public VillagerUtilsModule(final MewPlugin parent) {
        super(parent);
    }

    @Override protected void enable() {
        registerCommands();
    }

    private void registerCommands() {
        CommandRegistry commandRegistry = getParentPlugin().getCommandRegistry();

        Command.Builder<CommandSender> baseBuilder = commandRegistry
            .commandBuilder("mewutils")
            .literal("villager")
            .permission("mew.admin")
            .senderType(Player.class);

        Command<CommandSender> setRestocksToday = baseBuilder
            .literal("set")
            .argument(MultipleEntitySelectorArgument.of("selector"))
            .literal("restockstoday")
            .argument(IntegerArgument.of("amount"))
            .handler(commandContext -> {
                MultipleEntitySelector selector = commandContext.get("selector");
                int amount = commandContext.get("amount");
                for (Entity entity : selector.getEntities()) {
                    if (entity instanceof Villager villager)
                        villager.setRestocksToday(amount);
                }
            }).build();

        Command<CommandSender> setType = baseBuilder
            .literal("set")
            .argument(MultipleEntitySelectorArgument.of("selector"))
            .literal("type")
            .argument(EnumArgument.of(Villager.Type.class, "type"))
            .handler(commandContext -> {
                MultipleEntitySelector selector = commandContext.get("selector");
                Villager.Type type = commandContext.get("type");
                for (Entity entity : selector.getEntities()) {
                    if (entity instanceof Villager villager)
                        villager.setVillagerType(type);
                }
            }).build();

        Command<CommandSender> setLevel = baseBuilder
            .literal("set")
            .argument(MultipleEntitySelectorArgument.of("selector"))
            .literal("level")
            .argument(IntegerArgument.<CommandSender>builder("level").withMax(5).withMin(0))
            .handler(commandContext -> {
                MultipleEntitySelector selector = commandContext.get("selector");
                int level = commandContext.get("level");
                for (Entity entity : selector.getEntities()) {
                    if (entity instanceof Villager villager)
                        villager.setVillagerLevel(level);
                }
            }).build();

        Command<CommandSender> setExp = baseBuilder
            .literal("set")
            .argument(MultipleEntitySelectorArgument.of("selector"))
            .literal("exp")
            .argument(IntegerArgument.<CommandSender>builder("exp").withMin(0))
            .handler(commandContext -> {
                MultipleEntitySelector selector = commandContext.get("selector");
                int exp = commandContext.get("exp");
                for (Entity entity : selector.getEntities()) {
                    if (entity instanceof Villager villager)
                        villager.setVillagerExperience(exp);
                }
            }).build();

        Command<CommandSender> setProfession = baseBuilder
            .literal("set")
            .argument(MultipleEntitySelectorArgument.of("selector"))
            .literal("profession")
            .argument(EnumArgument.of(Villager.Profession.class, "profession"))
            .handler(commandContext -> {
                MultipleEntitySelector selector = commandContext.get("selector");
                Villager.Profession profession = commandContext.get("profession");

                for (Entity entity : selector.getEntities()) {
                    if (entity instanceof Villager villager)
                        villager.setProfession(profession);
                }
            }).build();

        Command<CommandSender> setReputation = baseBuilder
            .literal("set")
            .argument(MultipleEntitySelectorArgument.of("selector"))
            .literal("reputation")
            .argument(PlayerArgument.of("player"))
            .argument(EnumArgument.of(ReputationType.class, "reputationType"))
            .argument(IntegerArgument.<CommandSender>builder("reputationValue").withMin(0))
            .handler(commandContext -> {
                MultipleEntitySelector selector = commandContext.get("selector");
                Player player = commandContext.get("player");
                ReputationType repType = commandContext.get("reputationType");
                int repValue = commandContext.get("reputationValue");

                selector.getEntities().stream()
                    .filter(entity -> entity instanceof Villager)
                    .map(entity -> (Villager) entity)
                    .forEach(villager -> {
                        Reputation playerRep = Optional
                            .ofNullable(villager.getReputation(player.getUniqueId()))
                            .orElseGet(() -> new Reputation(new HashMap<>()));
                        playerRep.setReputation(repType, repValue);
                        villager.setReputation(player.getUniqueId(), playerRep);
                    });
            }).build();

        Command<CommandSender> restock = baseBuilder
            .literal("restock")
            .argument(MultipleEntitySelectorArgument.of("selector"))
            .handler(commandContext -> {
                MultipleEntitySelector selector = commandContext.get("selector");

                selector.getEntities().stream()
                    .filter(entity -> entity instanceof Villager)
                    .map(entity -> (Villager) entity)
                    .forEach(villager -> {
                        for (MerchantRecipe recipe : villager.getRecipes()) recipe.setUses(0);
                    });
            }).build();

        Command<CommandSender> view = baseBuilder
            .literal("view")
            .argument(SingleEntitySelectorArgument.of("selector"))
            .argument(PlayerArgument.of("player"))
            .handler(commandContext -> {
                SingleEntitySelector entity = commandContext.get("selector");
                Player player = commandContext.get("player");
                CommandSender sender = commandContext.getSender();

                if (entity.getEntity() instanceof Villager villager) {
                    sender.sendMessage("");
                    sender.sendMessage("Type: " + villager.getVillagerType().name());
                    sender.sendMessage("Level: " + villager.getVillagerLevel());
                    sender.sendMessage("Profession: " + villager.getProfession().name());
                    sender.sendMessage("RestocksToday: " + villager.getRestocksToday());
                    sender.sendMessage("Reputation:");
                    Reputation rep = villager.getReputation(player.getUniqueId());
                    if (rep == null) return;
                    for (ReputationType type : ReputationType.values()) {
                        sender.sendMessage(type + " : " + rep.getReputation(type));
                    }
                }
            }).build();

        commandRegistry.prepareCommands(
            setRestocksToday,
            setType,
            setLevel,
            setExp,
            setReputation,
            setProfession,
            restock,
            view
        );
    }

    @Override public boolean checkRequirement() {
        return true;
    }

}
