package cc.mewcraft.mewfishing.module.fishloot;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.mewfishing.MewFishing;
import cc.mewcraft.mewfishing.event.FishLootEvent;
import cc.mewcraft.mewfishing.loot.LootTableManager;
import cc.mewcraft.mewfishing.loot.api.Loot;
import cc.mewcraft.mewfishing.loot.api.LootTable;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.lucko.helper.random.VariableAmount;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Collection;

@Singleton
public class FishingListener implements AutoCloseableListener {
    private final MewFishing plugin;
    private final LootTableManager lootManager;

    @Inject
    public FishingListener(
        final MewFishing plugin,
        final LootTableManager lootManager
    ) {
        this.plugin = plugin;
        this.lootManager = lootManager;
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            // Only give loots when a fish (or item) is actually caught
            return;
        }

        if (VariableAmount.range(0D, 100D).getAmount() > plugin.config().customLootChance()) {
            // Don't give custom loots if it doesn't pass in the first place
            return;
        }

        FishLootEvent lootEvent = new FishLootEvent(event);

        // Draw a table
        LootTable table = lootManager.drawMatched(lootEvent);

        // Draw some loots from the table
        Collection<Loot> loots = table.drawMatched(lootEvent);

        // At least one loot will be drawn
        for (Loot loot : loots) {
            loot.apply(lootEvent);
        }
    }
}
