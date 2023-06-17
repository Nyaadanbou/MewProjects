package cc.mewcraft.townyorigin.listener;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.mewcore.util.ServerOriginUtils;
import cc.mewcraft.townyorigin.TownyOrigin;
import com.palmergames.bukkit.towny.event.PreNewTownEvent;
import com.palmergames.bukkit.towny.event.TownAddResidentEvent;
import com.palmergames.bukkit.towny.event.TownPreAddResidentEvent;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class TownyListener implements AutoCloseableListener {
    private final TownyOrigin plugin;

    public TownyListener(TownyOrigin plugin) {
        this.plugin = plugin;

        if (plugin.getServer().getPluginManager().getPlugin("Towny") == null) {
            plugin.getSLF4JLogger().warn("Towny is not installed on this server!");
        }
    }

    @EventHandler
    public void onJoinTown(TownAddResidentEvent event) {
        // We listen to the TownAddResidentEvent:
        // If it's the first time that the player joins a town,
        // we save the current server in his LuckPerms metadata.

        Resident resident = event.getResident();

        if (ServerOriginUtils.hasOrigin(resident.getUUID())) {
            // The player already has server-origin set
            plugin.getSLF4JLogger().info("Server origin already set: {}{uuid={}}", resident.getName(), resident.getUUID());
            return;
        }

        if (ServerOriginUtils.setOrigin(resident.getUUID())) {
            plugin.getSLF4JLogger().info("New server origin set: {}{uuid={}}", resident.getName(), resident.getUUID());
        }
    }

    @EventHandler
    public void OnPreJoinTown(TownPreAddResidentEvent event) {
        // We listen to the TownPreAddResidentEvent:
        // If the player already has a server origin,
        // we don't allow the player to join town.

        Resident resident = event.getResident();

        if (ServerOriginUtils.hasOrigin(resident.getUUID()) && !ServerOriginUtils.atOrigin(resident.getUUID())) {
            event.setCancelled(true);
            event.setCancelMessage(plugin.getLang().of("msg_cannot_join_town_outside_origin").replace("player", resident.getName()).plain());
        }
    }

    @EventHandler
    public void OnPreCreateTown(PreNewTownEvent event) {
        // We listen to the TownPreAddResidentEvent:
        // If the player already has a server origin,
        // we don't allow the player to create new town.

        Player player = event.getPlayer();

        if (ServerOriginUtils.hasOrigin(player.getUniqueId()) && !ServerOriginUtils.atOrigin(player.getUniqueId())) {
            event.setCancelled(true);
            event.setCancelMessage(plugin.getLang().of("msg_cannot_new_town_outside_origin").locale(player).plain());
        }
    }
}
