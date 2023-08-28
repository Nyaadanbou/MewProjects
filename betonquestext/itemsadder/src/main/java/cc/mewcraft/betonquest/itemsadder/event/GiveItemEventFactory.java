package cc.mewcraft.betonquest.itemsadder.event;

import cc.mewcraft.betonquest.itemsadder.util.ItemsAdderUtil;
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

public class GiveItemEventFactory implements EventFactory {
    private final BetonQuestLogger log;
    private final Server server;
    private final BukkitScheduler scheduler;
    private final Plugin plugin;

    public GiveItemEventFactory(final BetonQuestLogger log, final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.log = log;
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        int amount = instruction.getInt();
        String namespacedID = instruction.next() + ":" + instruction.next();
        ItemsAdderUtil.validateCustomStackSilently(instruction.getPackage(), namespacedID);
        return new PrimaryServerThreadEvent(
            new OnlineProfileRequiredEvent(
                log,
                new GiveItemEvent(namespacedID, amount),
                instruction.getPackage()
            ),
            server,
            scheduler,
            plugin
        );
    }
}
