package cc.mewcraft.betonquest.itemsadder.event;

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

public class PlayAnimationEventFactory implements EventFactory {
    private final BetonQuestLogger log;
    private final Server server;
    private final BukkitScheduler scheduler;
    private final Plugin plugin;

    public PlayAnimationEventFactory(final BetonQuestLogger log, final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.log = log;
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        String animation = instruction.next();
        return new PrimaryServerThreadEvent(
            new OnlineProfileRequiredEvent(
                log, new PlayAnimationEvent(animation), instruction.getPackage()
            ),
            server,
            scheduler,
            plugin
        );
    }
}
