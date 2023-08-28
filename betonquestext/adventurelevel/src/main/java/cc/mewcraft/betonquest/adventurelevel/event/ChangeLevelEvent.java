package cc.mewcraft.betonquest.adventurelevel.event;

import cc.mewcraft.adventurelevel.AdventureLevelProvider;
import cc.mewcraft.adventurelevel.data.PlayerData;
import cc.mewcraft.adventurelevel.level.category.Level;
import cc.mewcraft.adventurelevel.level.category.LevelCategory;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

public class ChangeLevelEvent implements Event {
    private final LevelCategory category;
    private final Integer amount;
    private final Mode mode;
    private final Boolean level;

    /*
     * Usage: adventurelevel <category> <amount> <set|add> level
     * */
    public ChangeLevelEvent(LevelCategory category, Integer amount, Mode mode, Boolean level) {
        this.category = category;
        this.amount = amount;
        this.mode = mode;
        this.level = level;
    }

    @Override public void execute(final Profile profile) throws QuestRuntimeException {
        PlayerData data = AdventureLevelProvider.get().getPlayerDataManager().load(profile.getPlayerUUID());
        Level level = data.getLevel(category);

        if (mode == Mode.ADD) {
            if (this.level) {
                level.addLevel(amount);
            } else {
                level.addExperience(amount);
            }
        } else {
            if (this.level) {
                level.setLevel(amount);
            } else {
                level.setExperience(amount);
            }
        }
    }

    enum Mode {
        SET, ADD
    }
}
