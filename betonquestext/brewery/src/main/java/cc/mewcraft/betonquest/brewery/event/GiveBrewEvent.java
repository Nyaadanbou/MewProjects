package cc.mewcraft.betonquest.brewery.event;

import cc.mewcraft.betonquest.variable.GenericVariable;
import com.dre.brewery.recipe.BRecipe;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class GiveBrewEvent implements Event {
    private final Integer amount;
    private final Integer quality;
    private final GenericVariable<BRecipe> recipe;

    public GiveBrewEvent(final Integer amount, final Integer quality, final GenericVariable<BRecipe> recipe) {
        this.amount = amount;
        this.quality = quality;
        this.recipe = recipe;
    }

    @Override public void execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().orElseThrow(() -> new QuestRuntimeException("Player is offline")).getPlayer();

        final ItemStack[] brews = new ItemStack[amount];
        for (int i = 0; i < amount; i++) {
            brews[i] = recipe.resolve(profile).create(quality);
        }

        final Collection<ItemStack> remaining = player.getInventory().addItem(brews).values();

        for (final ItemStack item : remaining) {
            player.getWorld().dropItem(player.getLocation(), item);
        }
    }
}