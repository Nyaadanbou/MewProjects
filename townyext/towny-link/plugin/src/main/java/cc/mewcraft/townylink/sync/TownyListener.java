package cc.mewcraft.townylink.sync;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.townylink.TownyLinkPlugin;
import cc.mewcraft.townylink.api.TownyLinkProvider;
import cc.mewcraft.townylink.sync.local.GlobalDataHolder;
import cc.mewcraft.townylink.sync.local.GovernmentObject;
import cc.mewcraft.townylink.sync.packet.GovernmentType;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.*;
import com.palmergames.bukkit.towny.event.nation.PreNewNationEvent;
import me.lucko.helper.promise.Promise;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerLoadEvent;

import java.util.UUID;

@Singleton
public class TownyListener implements AutoCloseableListener {
    private final TownyLinkPlugin plugin;
    private final GlobalDataHolder dataHolder;
    private final TownyMessenger messenger;

    @Inject
    public TownyListener(
        final TownyLinkPlugin plugin,
        final GlobalDataHolder dataHolder,
        final TownyMessenger messenger
    ) {
        this.plugin = plugin;
        this.dataHolder = dataHolder;
        this.messenger = messenger;
    }

    //<editor-fold desc="Start initial synchronization of Towny data">

    @EventHandler
    public void onServerStart(ServerLoadEvent event) {
        if (event.getType() != ServerLoadEvent.LoadType.STARTUP) {
            return;
        }

        Promise.start()

            // Report start of the process
            .thenRunDelayedSync(() -> plugin.getSLF4JLogger().info("Starting Towny data synchronization process ..."), 20)

            // Broadcast towns and nations from this server to the network
            .thenRunAsync(() -> {

                ImmutableSet.Builder<GovernmentObject> townData = ImmutableSet.builder();
                TownyAPI.getInstance().getTowns().forEach(town -> townData.add(new GovernmentObject(town.getUUID(), town.getName())));
                messenger.broadcastExisting(GovernmentType.TOWN, townData.build());

                ImmutableSet.Builder<GovernmentObject> nationData = ImmutableSet.builder();
                TownyAPI.getInstance().getNations().forEach(nation -> nationData.add(new GovernmentObject(nation.getUUID(), nation.getName())));
                messenger.broadcastExisting(GovernmentType.NATION, nationData.build());

            })

            // Request towns from other servers
            .thenComposeAsync(n -> TownyLinkProvider.get().requestGlobalTown())
            .thenAcceptAsync(data -> {
                data.stream().map(t -> new GovernmentObject(t.id(), t.name())).forEach(dataHolder::putTown);
                plugin.getSLF4JLogger().info("Received total {} towns from other servers", data.size());
            })

            // Request nations from other servers
            .thenComposeAsync(n -> TownyLinkProvider.get().requestGlobalNation())
            .thenAcceptAsync(data -> {
                data.stream().map(t -> new GovernmentObject(t.id(), t.name())).forEach(dataHolder::putNation);
                plugin.getSLF4JLogger().info("Received total {} nations from other servers", data.size());
            })

            // Report end of the process
            .thenRunAsync(() -> plugin.getSLF4JLogger().info("Synchronization process completed! Any errors have been reported above."));
    }

    //</editor-fold>

    //<editor-fold desc="Prevent duplicate towns/nations on creation">

    @EventHandler
    public void onPreNewTown(PreNewTownEvent event) {
        Player player = event.getPlayer();
        String name = event.getTownName();
        cancelTownName(event, player, name);
    }

    @EventHandler
    public void onPreNewNation(PreNewNationEvent event) {
        Player player = event.getTown().getMayor().getPlayer();
        String name = event.getNationName();
        cancelNationName(event, player, name);
    }

    //</editor-fold>

    //<editor-fold desc="Prevent duplicate towns/nations on renaming">

    @EventHandler
    public void onNationRename(NationPreRenameEvent event) {
        Player player = event.getNation().getKing().getPlayer();
        String name = event.getNewName();
        cancelNationName(event, player, name);
    }

    @EventHandler
    public void onTownPreRename(TownPreRenameEvent event) {
        Player player = event.getTown().getMayor().getPlayer();
        String name = event.getNewName();
        cancelTownName(event, player, name);
    }

    //</editor-fold>

    //<editor-fold desc="Sync town/nation names to other servers">

    @EventHandler
    public void onNewTown(NewTownEvent event) {
        handleNewGovernment(GovernmentType.TOWN, event.getTown().getUUID(), event.getTown().getName());
    }

    @EventHandler
    public void onNewNation(NewNationEvent event) {
        handleNewGovernment(GovernmentType.NATION, event.getNation().getUUID(), event.getNation().getName());
    }

    @EventHandler
    public void onRenameTown(RenameTownEvent event) {
        handleNewGovernment(GovernmentType.TOWN, event.getTown().getUUID(), event.getTown().getName());
    }

    @EventHandler
    public void onRenameNation(RenameNationEvent event) {
        handleNewGovernment(GovernmentType.NATION, event.getNation().getUUID(), event.getNation().getName());
    }

    @EventHandler
    public void onDeleteTown(DeleteTownEvent event) {
        handleDeleteGovernment(GovernmentType.TOWN, event.getTownUUID());
    }

    @EventHandler
    public void onDeleteNation(DeleteNationEvent event) {
        handleDeleteGovernment(GovernmentType.NATION, event.getNationUUID());
    }

    //</editor-fold>

    /**
     * Convenient method to handle government delete.
     */
    private void handleDeleteGovernment(GovernmentType type, UUID uuid) {
        messenger.broadcastDelete(type, uuid);
    }

    /**
     * Convenient method to handle government creation.
     */
    private void handleNewGovernment(GovernmentType type, UUID uuid, String name) {
        messenger.broadcastNew(type, uuid, name);
    }

    /**
     * Convenient method to cancel new town.
     */
    private void cancelTownName(CancellableTownyEvent event, Player player, String name) {
        if (dataHolder.hasTown(name)) {
            plugin.getSLF4JLogger().info("Cancelled new town due to duplicate name: {}", name);
            event.setCancelled(true);
            event.setCancelMessage(plugin.getLang().of("town_name_already_exists")
                .replace("town", name)
                .locale(player)
                .plain()
            );
        }
    }

    /**
     * Convenient method to cancel new nation.
     */
    private void cancelNationName(CancellableTownyEvent event, Player player, String name) {
        if (dataHolder.hasNation(name)) {
            plugin.getSLF4JLogger().info("Cancelled new nation due to duplicate name: {}", name);
            event.setCancelled(true);
            event.setCancelMessage(plugin.getLang().of("nation_name_already_exists")
                .replace("nation", name)
                .locale(player)
                .plain()
            );
        }
    }
}
