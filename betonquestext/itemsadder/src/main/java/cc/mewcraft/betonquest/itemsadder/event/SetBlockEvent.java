package cc.mewcraft.betonquest.itemsadder.event;

import cc.mewcraft.betonquest.itemsadder.util.ItemsAdderUtil;
import dev.lone.itemsadder.api.CustomBlock;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.LocationData;
import org.bukkit.Location;

public class SetBlockEvent implements Event {
    private final String namespacedID;
    private final LocationData locationData;

    public SetBlockEvent(final String namespacedID, final LocationData locationData) {
        this.namespacedID = namespacedID;
        this.locationData = locationData;
    }

    @Override public void execute(Profile profile) throws QuestRuntimeException {
        CustomBlock cs = CustomBlock.getInstance(namespacedID);
        Location location = locationData.get(profile);
        cs.place(location);
    }
}
