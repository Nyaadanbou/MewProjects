package cc.mewcraft.betonquest.itemsadder;

import dev.lone.itemsadder.api.ItemsAdder;
import lombok.CustomLog;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

@CustomLog
public class PlayAnimationEvent extends QuestEvent {

    private final String animation;

    public PlayAnimationEvent(Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        animation = instruction.next();
    }

    @Override
    protected Void execute(Profile profile) throws QuestRuntimeException {
        ItemsAdder.playTotemAnimation(
            profile.getOnlineProfile()
                .orElseThrow(() -> new QuestRuntimeException("Player is offline"))
                .getPlayer(),
            animation);
        return null;
    }

}
