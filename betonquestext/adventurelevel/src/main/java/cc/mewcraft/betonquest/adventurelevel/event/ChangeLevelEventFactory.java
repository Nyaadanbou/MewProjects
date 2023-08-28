package cc.mewcraft.betonquest.adventurelevel.event;

import cc.mewcraft.adventurelevel.level.category.LevelCategory;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.OnlineProfileRequiredEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class ChangeLevelEventFactory implements EventFactory {
    private final BetonQuestLogger log;
    private final Server server;
    private final BukkitScheduler scheduler;
    private final Plugin plugin;

    public ChangeLevelEventFactory(final BetonQuestLogger log, final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.log = log;
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        LevelCategory category = instruction.getEnum(LevelCategory.class);
        Integer amount = instruction.getInt();
        ChangeLevelEvent.Mode mode = instruction.getEnum(ChangeLevelEvent.Mode.class);
        Boolean level = instruction.hasArgument("level");
        return new PrimaryServerThreadEvent(
            new OnlineProfileRequiredEvent(log, new ChangeLevelEvent(category, amount, mode, level), instruction.getPackage()),
            server, scheduler, plugin
        );
    }
}
