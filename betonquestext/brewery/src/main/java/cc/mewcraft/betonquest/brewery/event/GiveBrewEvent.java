package cc.mewcraft.betonquest.brewery.event;

import cc.mewcraft.betonquest.variable.GenericVariable;
import com.dre.brewery.recipe.BRecipe;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class GiveBrewEvent extends QuestEvent {

    private final Integer amount;
    private final Integer quality;
    private final GenericVariable<BRecipe> recipe;

    public GiveBrewEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        amount = instruction.getInt();

        quality = instruction.getInt();

        if (quality < 0 || quality > 10) {
            throw new InstructionParseException("Brew quality must be between 0 and 10!");
        }

        recipe = new GenericVariable<>(
            instruction.next().replace("_", " "),
            instruction.getPackage(),
            recipeName -> {
                BRecipe recipe = null;
                for (final BRecipe r : BRecipe.getAllRecipes()) {
                    if (r.hasName(recipeName)) {
                        recipe = r;
                        break;
                    }
                }
                if (recipe == null) {
                    throw new QuestRuntimeException("There is no brewing recipe with the name " + "!");
                } else {
                    return recipe;
                }
            });
    }

    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().orElseThrow(() -> new QuestRuntimeException("Player is offline")).getPlayer();

        final ItemStack[] brews = new ItemStack[amount];
        for (int i = 0; i < amount; i++) {
            brews[i] = recipe.resolve(profile).create(quality);
        }

        final Collection<ItemStack> remaining = player.getInventory().addItem(brews).values();

        for (final ItemStack item : remaining) {
            player.getWorld().dropItem(player.getLocation(), item);
        }
        return null;
    }
}