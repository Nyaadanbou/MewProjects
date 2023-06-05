package cc.mewcraft.mewfishing.module;

import cc.mewcraft.mewfishing.MewFishing;
import cc.mewcraft.mewfishing.event.FishLootEvent;
import cc.mewcraft.mewfishing.loot.LootTableManager;
import cc.mewcraft.mewfishing.loot.api.Loot;
import cc.mewcraft.mewfishing.loot.api.LootTable;
import me.lucko.helper.Events;
import me.lucko.helper.random.VariableAmount;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.event.player.PlayerFishEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static org.bukkit.event.player.PlayerFishEvent.State;

public class FishLootModule implements TerminableModule {

    private final MewFishing plugin;
    private final LootTableManager lootManager;

    public FishLootModule(final MewFishing plugin) {
        this.plugin = plugin;
        this.lootManager = new LootTableManager(plugin);
    }

    @Override public void setup(final @NotNull TerminableConsumer consumer) {
        Events.subscribe(PlayerFishEvent.class).handler(this::onFish).bindWith(consumer);
    }

    public LootTableManager getLootManager() {
        return lootManager;
    }

    private void onFish(PlayerFishEvent event) {
        if (event.getState() != State.CAUGHT_FISH) {
            return; // only give loots when something is actually caught
        }

        if (VariableAmount.range(0D, 100D).getAmount() > plugin.config().customLootChance()) {
            return; // don't give custom loots if it doesn't pass in the first place
        }

        FishLootEvent lootEvent = new FishLootEvent(event);
        if (!event.callEvent()) {
            plugin.log("FishLootEvent was cancelled");
            return;
        }

        LootTable table = lootManager.drawMatched(lootEvent); // draw a table
        Collection<Loot> loots = table.drawAll(lootEvent); // draw some loots from the table
        loots.forEach(loot -> loot.apply(lootEvent)); // at least one loot will be drawn
    }

}
