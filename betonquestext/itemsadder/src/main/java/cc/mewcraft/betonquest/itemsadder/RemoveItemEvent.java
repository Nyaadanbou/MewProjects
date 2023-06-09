package cc.mewcraft.betonquest.itemsadder;

import cc.mewcraft.betonquest.itemsadder.util.ItemsAdderUtil;
import dev.lone.itemsadder.api.CustomStack;
import lombok.CustomLog;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.inventory.ItemStack;

@CustomLog
public class RemoveItemEvent extends QuestEvent {

    private final String namespacedID;
    private final int amount;

    public RemoveItemEvent(Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        amount = instruction.getInt();
        namespacedID = instruction.next() + ":" + instruction.next();
        ItemsAdderUtil.validateCustomStackSilently(instruction.getPackage(), namespacedID);
    }

    @Override
    protected Void execute(Profile profile) throws QuestRuntimeException {
        ItemStack is = CustomStack.getInstance(namespacedID).getItemStack();
        is.setAmount(amount);
        profile.getOnlineProfile().orElseThrow(() -> new QuestRuntimeException("Player is offline"))
            .getPlayer().getInventory().removeItem(is);
        return null;
    }

}
