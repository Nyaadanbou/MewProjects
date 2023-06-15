package cc.mewcraft.townyorigin.listener;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.mewcore.util.ServerOriginUtils;
import cc.mewcraft.townyorigin.TownyOrigin;
import com.palmergames.bukkit.towny.event.TownAddResidentEvent;
import com.palmergames.bukkit.towny.object.Resident;
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

        boolean success = ServerOriginUtils.setOrigin(resident.getUUID());
        if (success) {
            plugin.getSLF4JLogger().info("New server origin set: {}{uuid={}}", resident.getName(), resident.getUUID());
        }
    }
}
