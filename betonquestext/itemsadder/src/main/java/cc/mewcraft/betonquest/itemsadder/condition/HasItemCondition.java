package cc.mewcraft.betonquest.itemsadder.condition;

import cc.mewcraft.betonquest.itemsadder.util.ItemsAdderUtil;
import dev.lone.itemsadder.api.CustomStack;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.inventory.ItemStack;

public class HasItemCondition extends Condition {

    private final String namespacedID;
    private final int amount;

    public HasItemCondition(Instruction instruction) throws InstructionParseException {
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
        ItemStack[] inventoryItems = profile.getOnlineProfile().orElseThrow(() -> new QuestRuntimeException("Player is offline"))
            .getPlayer().getInventory().getContents();
        int has = 0;
        for (ItemStack it : inventoryItems) {
            CustomStack cs = CustomStack.byItemStack(it);
            if (cs != null && cs.getNamespacedID().equalsIgnoreCase(namespacedID)) {
                if (it.getAmount() >= amount) {
                    return true;
                } else {
                    has += it.getAmount();
                }
            }
            if (has >= amount) {
                return true;
            }
        }
        return false;
    }
}
