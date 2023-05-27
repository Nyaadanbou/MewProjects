package cc.mewcraft.adventurelevel.listener;

import cc.mewcraft.adventurelevel.AdventureLevel;
import cc.mewcraft.adventurelevel.data.PlayerDataManager;
import cc.mewcraft.adventurelevel.level.category.LevelBean;
import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.google.inject.Inject;
import org.bukkit.event.EventHandler;

import static org.bukkit.event.EventPriority.HIGH;

/**
 * This listener is the entry point of our level system.
 */
public class PickupExpListener implements AutoCloseableListener {

    private final AdventureLevel plugin;
    private final PlayerDataManager playerDataManager;

    @Inject
    public PickupExpListener(final AdventureLevel plugin, final PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.playerDataManager = playerDataManager;
    }

    @EventHandler(priority = HIGH, ignoreCancelled = true)
    public void onPickupExp(PlayerPickupExperienceEvent event) {
        playerDataManager
            .load(event.getPlayer())
            .thenAcceptSync(playerData -> {
                // Handle main level
                playerData.getMainLevel().handleEvent(event);

                // Handle categorical levels
                LevelBean.Category levelCategory = LevelBean.Category.toLevelCategory(event.getExperienceOrb().getSpawnReason());
                if (levelCategory != null) {
                    playerData.getCateLevel(levelCategory).handleEvent(event);
                }
            });
    }
}
