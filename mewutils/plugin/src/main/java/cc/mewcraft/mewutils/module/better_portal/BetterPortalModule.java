package cc.mewcraft.mewutils.module.better_portal;

import cc.mewcraft.mewutils.api.MewPlugin;
import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.mewutils.api.module.ModuleBase;
import com.google.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPortalEvent;

public class BetterPortalModule extends ModuleBase implements AutoCloseableListener {

    @Inject
    public BetterPortalModule(final MewPlugin parent) {
        super(parent);
    }

    @Override protected void enable() {
        registerListener(this);
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        if (event.getTo().getWorld().getWorldBorder().isInside(event.getTo()))
            return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        getLang().of("cancelled").send(player);
    }

    @Override public boolean checkRequirement() {
        return true;
    }

}
