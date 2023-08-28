package cc.mewcraft.betonquest.itemsadder.event;

import dev.lone.itemsadder.api.ItemsAdder;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

public class PlayAnimationEvent implements Event {
    private final String animation;

    public PlayAnimationEvent(final String animation) {
        this.animation = animation;
    }

    @Override public void execute(Profile profile) throws QuestRuntimeException {
        ItemsAdder.playTotemAnimation(
            profile.getOnlineProfile()
                .orElseThrow(() -> new QuestRuntimeException("Player is offline"))
                .getPlayer(),
            animation);
    }
}
