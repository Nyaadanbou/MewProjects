package cc.mewcraft.townylink.listener;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.townylink.TownyLink;
import cc.mewcraft.townylink.messager.Action;
import cc.mewcraft.townylink.messager.Messenger;
import cc.mewcraft.townylink.object.TownyRepository;
import com.google.inject.Inject;
import com.palmergames.bukkit.towny.event.*;
import com.palmergames.bukkit.towny.event.nation.PreNewNationEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class TownyListener implements AutoCloseableListener {

    private final TownyLink plugin;
    private final Messenger messenger;
    private final TownyRepository repository;

    @Inject
    public TownyListener(
        final TownyLink plugin,
        final Messenger messenger,
        final TownyRepository repository
    ) {
        this.plugin = plugin;
        this.messenger = messenger;
        this.repository = repository;
    }

    //// Prevent duplicate towns/nations on creation ////

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

    //// Prevent duplicate towns/nations on renaming ////

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

    //// Sync town/nation names to other servers ////

    @EventHandler
    public void onNewTown(NewTownEvent event) {
        String name = event.getTown().getName();
        this.repository.addTownName(name);
        this.messenger.sendMessage(Action.ADD_TOWN, name);
    }

    @EventHandler
    public void onNewNation(NewNationEvent event) {
        String name = event.getNation().getName();
        this.repository.addNationName(name);
        this.messenger.sendMessage(Action.ADD_NATION, name);
    }

    @EventHandler
    public void onDeleteTown(DeleteTownEvent event) {
        String name = event.getTownName();
        this.repository.removeTownName(name);
        this.messenger.sendMessage(Action.DELETE_TOWN, name);
    }

    @EventHandler
    public void onDeleteNation(DeleteNationEvent event) {
        String name = event.getNationName();
        this.repository.removeNationName(name);
        this.messenger.sendMessage(Action.DELETE_NATION, name);
    }

    @EventHandler
    public void onRenameTown(RenameTownEvent event) {
        String oldName = event.getOldName();
        String newName = event.getTown().getName();
        this.repository.addTownName(newName);
        this.messenger.sendMessage(Action.ADD_TOWN, newName);
        this.repository.removeTownName(oldName);
        this.messenger.sendMessage(Action.DELETE_TOWN, oldName);
    }

    @EventHandler
    public void onRenameNation(RenameNationEvent event) {
        String oldName = event.getOldName();
        String newName = event.getNation().getName();
        this.repository.addNationName(newName);
        this.messenger.sendMessage(Action.ADD_NATION, newName);
        this.repository.removeNationName(oldName);
        this.messenger.sendMessage(Action.DELETE_NATION, oldName);
    }

    /**
     * Convenient method.
     */
    private void cancelTownName(final CancellableTownyEvent event, final Player player, final String name) {
        if (this.repository.hasTownName(name)) {
            this.plugin.getLogger().info("Cancelled duplicate town creation: " + name);
            event.setCancelled(true);
            event.setCancelMessage(this.plugin.getLang().of("town_name_already_exists")
                .replace("town", name)
                .locale(player)
                .plain()
            );
        }
    }

    /**
     * Convenient method.
     */
    private void cancelNationName(final CancellableTownyEvent event, final Player player, final String name) {
        if (this.repository.hasNationName(name)) {
            this.plugin.getLogger().info("Cancelled duplicate nation creation: " + name);
            event.setCancelled(true);
            event.setCancelMessage(this.plugin.getLang().of("nation_name_already_exists")
                .replace("nation", name)
                .locale(player)
                .plain()
            );
        }
    }

}
