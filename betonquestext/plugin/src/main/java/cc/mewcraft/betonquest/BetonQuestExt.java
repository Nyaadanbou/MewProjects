package cc.mewcraft.betonquest;

import cc.mewcraft.betonquest.adventurelevel.condition.ReachLevelCondition;
import cc.mewcraft.betonquest.adventurelevel.event.ChangeLevelEvent;
import cc.mewcraft.betonquest.adventurelevel.event.ChangeLevelEventFactory;
import cc.mewcraft.betonquest.brewery.condition.HasBrewCondition;
import cc.mewcraft.betonquest.brewery.event.GiveBrewEvent;
import cc.mewcraft.betonquest.brewery.event.GiveBrewEventFactory;
import cc.mewcraft.betonquest.brewery.event.TakeBrewEvent;
import cc.mewcraft.betonquest.brewery.event.TakeBrewEventFactory;
import cc.mewcraft.betonquest.itemsadder.condition.ArmorCondition;
import cc.mewcraft.betonquest.itemsadder.condition.BlockCondition;
import cc.mewcraft.betonquest.itemsadder.condition.HasItemCondition;
import cc.mewcraft.betonquest.itemsadder.condition.HasItemInHandCondition;
import cc.mewcraft.betonquest.itemsadder.event.GiveItemEvent;
import cc.mewcraft.betonquest.itemsadder.event.PlayAnimationEvent;
import cc.mewcraft.betonquest.itemsadder.event.RemoveItemEvent;
import cc.mewcraft.betonquest.itemsadder.event.RemoveItemEventFactory;
import cc.mewcraft.betonquest.itemsadder.event.SetBlockEvent;
import cc.mewcraft.betonquest.itemsadder.objective.BreakBlockObjective;
import cc.mewcraft.betonquest.itemsadder.objective.EnchantObjective;
import cc.mewcraft.betonquest.itemsadder.objective.PickupObjective;
import cc.mewcraft.betonquest.itemsadder.objective.PlaceBlockObjective;
import cc.mewcraft.betonquest.itemsadder.objective.SmeltObjective;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;

public class BetonQuestExt extends ExtendedJavaPlugin {
    private BetonQuestLoggerFactory loggerFactory;

    @Override
    public void enable() {
        // Load config
        saveDefaultConfig();

        // Init logger
        loggerFactory = getService(BetonQuestLoggerFactory.class);

        // Brewery
        if (isPluginPresent("Brewery")) {
            BetonQuest.getInstance().registerConditions("hasbrew", HasBrewCondition.class);
            BetonQuest.getInstance().registerNonStaticEvent("takebrew", new TakeBrewEventFactory(loggerFactory.create(TakeBrewEvent.class), getServer(), getServer().getScheduler(), this));
            BetonQuest.getInstance().registerNonStaticEvent("givebrew", new GiveBrewEventFactory(loggerFactory.create(GiveBrewEvent.class), getServer(), getServer().getScheduler(), this));
        }

        // ItemsAdder
        if (isPluginPresent("ItemsAdder")) {
            BetonQuest.getInstance().registerConditions("iahas", HasItemCondition.class);
            BetonQuest.getInstance().registerConditions("iaarmor", ArmorCondition.class);
            BetonQuest.getInstance().registerConditions("iahand", HasItemInHandCondition.class);
            BetonQuest.getInstance().registerConditions("iablock", BlockCondition.class);
            BetonQuest.getInstance().registerNonStaticEvent("iaremove", new RemoveItemEventFactory(loggerFactory.create(RemoveItemEvent.class), getServer(), getServer().getScheduler(), this));
            BetonQuest.getInstance().registerNonStaticEvent("iagive", new RemoveItemEventFactory(loggerFactory.create(GiveItemEvent.class), getServer(), getServer().getScheduler(), this));
            BetonQuest.getInstance().registerNonStaticEvent("iasetblock", new RemoveItemEventFactory(loggerFactory.create(SetBlockEvent.class), getServer(), getServer().getScheduler(), this));
            BetonQuest.getInstance().registerNonStaticEvent("iaplay", new RemoveItemEventFactory(loggerFactory.create(PlayAnimationEvent.class), getServer(), getServer().getScheduler(), this));
            BetonQuest.getInstance().registerObjectives("iapickup", PickupObjective.class);
            BetonQuest.getInstance().registerObjectives("iabreak", BreakBlockObjective.class);
            BetonQuest.getInstance().registerObjectives("iaplace", PlaceBlockObjective.class);
            BetonQuest.getInstance().registerObjectives("iaenchant", EnchantObjective.class);
            BetonQuest.getInstance().registerObjectives("iasmelt", SmeltObjective.class);
        }

        // AdventureLevel
        if (isPluginPresent("AdventureLevel")) {
            BetonQuest.getInstance().registerConditions("adventurelevel", ReachLevelCondition.class);
            BetonQuest.getInstance().registerNonStaticEvent("adventurelevel", new ChangeLevelEventFactory(loggerFactory.create(ChangeLevelEvent.class), getServer(), getServer().getScheduler(), this));
        }

        // Reload when itemsadder data load done
        /*Events.subscribe(ItemsAdderLoadDataEvent.class)
            .handler(e -> BetonQuest.getInstance().reload())
            .bindWith(this);*/
    }
}
