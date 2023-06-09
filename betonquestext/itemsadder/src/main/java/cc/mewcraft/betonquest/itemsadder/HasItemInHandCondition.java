package cc.mewcraft.betonquest.itemsadder;

import cc.mewcraft.betonquest.itemsadder.util.ItemsAdderUtil;
import dev.lone.itemsadder.api.CustomStack;
import lombok.CustomLog;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.inventory.ItemStack;

@CustomLog
public class HasItemInHandCondition extends Condition {

    private final String namespacedID;
    private final int amount;

    public HasItemInHandCondition(Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        amount = instruction.getInt();
        if (amount < 1) {
            throw new InstructionParseException("Amount cannot be less than 1");
        }
        namespacedID = instruction.next() + ":" + instruction.next();
        ItemsAdderUtil.validateCustomStackSilently(instruction.getPackage(), namespacedID);
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        ItemStack handItem = profile.getOnlineProfile().orElseThrow(() -> new QuestRuntimeException("Player is offline"))
            .getPlayer().getInventory().getItemInMainHand();
        CustomStack cs = CustomStack.byItemStack(handItem);
        if (cs != null && cs.getNamespacedID().equalsIgnoreCase(namespacedID)) {
            return handItem.getAmount() >= amount;
        }
        return false;
    }
}
