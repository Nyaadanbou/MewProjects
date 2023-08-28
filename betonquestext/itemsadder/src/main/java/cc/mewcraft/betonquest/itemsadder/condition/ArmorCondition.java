package cc.mewcraft.betonquest.itemsadder.condition;

import cc.mewcraft.betonquest.itemsadder.util.ItemsAdderUtil;
import dev.lone.itemsadder.api.CustomStack;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.inventory.ItemStack;

public class ArmorCondition extends Condition {
    private final String namespacedID;

    public ArmorCondition(Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        namespacedID = instruction.next() + ":" + instruction.next();
        ItemsAdderUtil.validateCustomStackSilently(instruction.getPackage(), namespacedID);
    }

    @Override
    protected Boolean execute(Profile profile) throws QuestRuntimeException {
        ItemStack[] inventoryItems = profile.getOnlineProfile().orElseThrow(() -> new QuestRuntimeException("Player is offline"))
            .getPlayer().getInventory().getArmorContents();
        for (ItemStack is : inventoryItems) {
            CustomStack cs = CustomStack.byItemStack(is);
            if (cs != null && cs.getNamespacedID().equalsIgnoreCase(namespacedID)) {
                return true;
            }
        }
        return false;
    }

}
