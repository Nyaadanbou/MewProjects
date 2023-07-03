package cc.mewcraft.betonquest.adventurelevel.event;

import cc.mewcraft.adventurelevel.AdventureLevelProvider;
import cc.mewcraft.adventurelevel.data.PlayerData;
import cc.mewcraft.adventurelevel.level.category.LevelBean;
import cc.mewcraft.adventurelevel.level.category.LevelCategory;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

public class ChangeLevelEvent extends QuestEvent {
    private final LevelCategory category;
    private final Integer amount;
    private final Mode mode;
    private final Boolean level;

    // Usage: alevel <category> <amount> <set|add> level
    public ChangeLevelEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        category = instruction.getEnum(LevelCategory.class);
        amount = instruction.getInt();
        mode = instruction.getEnum(Mode.class);
        level = instruction.hasArgument("level");
    }

    @Override protected Void execute(final Profile profile) throws QuestRuntimeException {
        PlayerData data = AdventureLevelProvider.get().getPlayerDataManager().load(profile.getPlayerUUID());
        LevelBean level = data.getLevelBean(category);

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

        return null;
    }

    enum Mode {
        SET, ADD
    }
}
