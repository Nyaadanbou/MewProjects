package cc.mewcraft.betonquest.itemsadder;

import cc.mewcraft.betonquest.itemsadder.util.ItemsAdderUtil;
import dev.lone.itemsadder.api.CustomBlock;
import lombok.CustomLog;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.LocationData;
import org.bukkit.Location;

@CustomLog
public class BlockCondition extends Condition {

    private final String namespacedID;
    private final LocationData locationData;

    public BlockCondition(Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        namespacedID = instruction.next() + ":" + instruction.next();
        ItemsAdderUtil.validateCustomBlockSilently(instruction.getPackage(), namespacedID);
        locationData = instruction.getLocation().getLocationData();
    }

    @Override
    protected Boolean execute(Profile profile) throws QuestRuntimeException {
        Location location = locationData.get(profile);
        CustomBlock cb = CustomBlock.byAlreadyPlaced(location.getBlock());
        return cb != null && cb.getNamespacedID().equalsIgnoreCase(namespacedID);
    }

}
