package cc.mewcraft.betonquest;

import cc.mewcraft.betonquest.adventurelevel.condition.ReachLevelCondition;
import cc.mewcraft.betonquest.adventurelevel.event.ChangeLevelEvent;
import cc.mewcraft.betonquest.brewery.condition.HasBrewCondition;
import cc.mewcraft.betonquest.brewery.event.GiveBrewEvent;
import cc.mewcraft.betonquest.brewery.event.TakeBrewEvent;
import cc.mewcraft.betonquest.itemsadder.*;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.betonquest.betonquest.BetonQuest;

public class BetonQuestExt extends ExtendedJavaPlugin {
    @Override
    public void enable() {
        // Load config
        saveDefaultConfig();

        // Brewery
        if (isPluginPresent("Brewery")) {
            BetonQuest.getInstance().registerConditions("hasbrew", HasBrewCondition.class);
            BetonQuest.getInstance().registerEvents("takebrew", TakeBrewEvent.class);
            BetonQuest.getInstance().registerEvents("givebrew", GiveBrewEvent.class);
        }

        // ItemsAdder
        if (isPluginPresent("ItemsAdder")) {
            BetonQuest.getInstance().registerConditions("iahas", HasItemCondition.class);
            BetonQuest.getInstance().registerConditions("iaarmor", ArmorCondition.class);
            BetonQuest.getInstance().registerConditions("iahand", HasItemInHandCondition.class);
            BetonQuest.getInstance().registerConditions("iablock", BlockCondition.class);
            BetonQuest.getInstance().registerEvents("iaremove", RemoveItemEvent.class);
            BetonQuest.getInstance().registerEvents("iagive", GiveItemEvent.class);
            BetonQuest.getInstance().registerEvents("iasetblock", SetBlockEvent.class);
            BetonQuest.getInstance().registerEvents("iaplay", PlayAnimationEvent.class);
            BetonQuest.getInstance().registerObjectives("iapickup", PickupObjective.class);
            BetonQuest.getInstance().registerObjectives("iabreak", BreakBlockObjective.class);
            BetonQuest.getInstance().registerObjectives("iaplace", PlaceBlockObjective.class);
            BetonQuest.getInstance().registerObjectives("iaenchant", EnchantObjective.class);
            BetonQuest.getInstance().registerObjectives("iasmelt", SmeltObjective.class);
        }

        // AdventureLevel
        if (isPluginPresent("AdventureLevel")) {
            BetonQuest.getInstance().registerConditions("alevel", ReachLevelCondition.class);
            BetonQuest.getInstance().registerEvents("alevel", ChangeLevelEvent.class);
        }

        // Reload when itemsadder data load done
        // Events.subscribe(ItemsAdderLoadDataEvent.class)
        //         .handler(e -> BetonQuest.getInstance().reload())
        //         .bindWith(this);
    }
}
