package cc.mewcraft.townylink.sync;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.townylink.TownyLinkPlugin;
import cc.mewcraft.townylink.sync.local.GlobalDataHolder;
import cc.mewcraft.townylink.sync.packet.GovernmentType;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.palmergames.bukkit.towny.event.*;
import com.palmergames.bukkit.towny.event.nation.PreNewNationEvent;
import me.lucko.helper.Schedulers;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Singleton
public class TownyListener implements AutoCloseableListener, TerminableModule {
    private final TownyLinkPlugin plugin;
    private final GlobalDataHolder holder;
    private final TownyMessenger messenger;

    @Inject
    public TownyListener(
        final TownyLinkPlugin plugin,
        final GlobalDataHolder holder,
        final TownyMessenger messenger
    ) {
        this.plugin = plugin;
        this.holder = holder;
        this.messenger = messenger;
    }

    @Override public void setup(final @NotNull TerminableConsumer consumer) {
        consumer.bind(this);
        consumer.bindModule(messenger);

        // Start initial synchronization of global data in the network
        Schedulers.bukkit().scheduleSyncDelayedTask(plugin, messenger::sync, 20);
    }

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
        if (holder.hasTown(name)) {
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
        if (holder.hasNation(name)) {
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
