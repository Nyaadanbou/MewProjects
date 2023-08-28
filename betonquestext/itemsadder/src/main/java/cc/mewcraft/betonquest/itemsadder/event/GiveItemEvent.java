package cc.mewcraft.betonquest.itemsadder.event;

import dev.lone.itemsadder.api.CustomStack;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.inventory.ItemStack;

public class GiveItemEvent implements Event {
    private final String namespacedID;
    private final int amount;

    public GiveItemEvent(final String namespacedID, final int amount) {
        this.namespacedID = namespacedID;
        this.amount = amount;
    }

    @Override public void execute(Profile profile) throws QuestRuntimeException {
        ItemStack is = CustomStack.getInstance(namespacedID).getItemStack();
        is.setAmount(amount);
        profile.getOnlineProfile()
            .orElseThrow(() -> new QuestRuntimeException("Player is offline"))
            .getPlayer()
            .getInventory()
            .addItem(is);
    }

}
