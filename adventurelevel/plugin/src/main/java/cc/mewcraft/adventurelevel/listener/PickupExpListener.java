package cc.mewcraft.adventurelevel.listener;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import cc.mewcraft.adventurelevel.data.PlayerDataManager;
import cc.mewcraft.adventurelevel.level.category.LevelCategory;
import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.event.EventHandler;

import static org.bukkit.event.EventPriority.HIGH;

/**
 * This listener is the entry point of our level system.
 */
@Singleton
public class PickupExpListener implements AutoCloseableListener {

    private final AdventureLevelPlugin plugin;
    private final PlayerDataManager playerDataManager;

    @Inject
    public PickupExpListener(final AdventureLevelPlugin plugin, final PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.playerDataManager = playerDataManager;
    }

    @EventHandler(priority = HIGH, ignoreCancelled = true)
    public void onPickupExp(PlayerPickupExperienceEvent event) {
        playerDataManager.load(event.getPlayer()).thenAcceptSync(playerData -> {
            // Handle main level
            playerData.getLevelBean(LevelCategory.MAIN).handleEvent(event);

            // Handle other levels
            LevelCategory levelCategory = LevelCategory.toLevelCategory(event.getExperienceOrb().getSpawnReason());
            if (levelCategory != null) {
                playerData.getLevelBean(levelCategory).handleEvent(event);
            }
        });
    }
}
