package cc.mewcraft.townyorigin.listener;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.mewcore.util.ServerOriginUtils;
import cc.mewcraft.townyorigin.TownyOrigin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerListener implements AutoCloseableListener {

    private final TownyOrigin plugin;

    public PlayerListener(TownyOrigin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        // We listen to the PlayerDeathEvent:
        // If the player is in his server-origin,
        // then don't drop his items (exp still drops)

        Player player = event.getPlayer();

        if (event.getKeepInventory() && event.getDrops().isEmpty()) {
            // The keepInventory is already set to true by other plugins:
            // We simply notify the player that he is blessed
            plugin.getLang().of("msg_inventory_kept_for_other_reasons").title(player);
            return;
        }

        if (ServerOriginUtils.getOrigin(player.getUniqueId()) == null) {
            plugin.getLang().of("msg_inventory_not_kept_for_none_origin").title(player);
            return; // The player doesn't have server-origin-id set
        }

        if (ServerOriginUtils.atOrigin(player.getUniqueId())) {
            // The player is in the server-origin:
            event.setKeepInventory(true);
            event.getDrops().clear();
            plugin.getLang().of("msg_inventory_kept_for_inside_origin").title(player);
        } else {
            plugin.getLang().of("msg_inventory_not_kept_for_outside_origin").title(player);
        }
    }

}
