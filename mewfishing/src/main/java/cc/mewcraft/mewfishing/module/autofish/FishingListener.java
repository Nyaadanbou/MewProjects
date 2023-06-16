package cc.mewcraft.mewfishing.module.autofish;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.mewfishing.MewFishing;
import cc.mewcraft.mewfishing.event.AutoFishEvent;
import cc.mewcraft.mewfishing.util.PlayerActions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.lucko.helper.Schedulers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;

@Singleton
public class FishingListener implements AutoCloseableListener {
    private final MewFishing plugin;

    @Inject
    public FishingListener(
        final MewFishing plugin
    ) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();

        if (!player.hasPermission("mewfishing.auto.use")) {
            return;
        }
        if (!new AutoFishEvent(player, event.getHook()).callEvent()) {
            return;
        }

        if (event.getState() == PlayerFishEvent.State.BITE || event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Schedulers.builder()
                .sync()
                .after(event.getState() == PlayerFishEvent.State.BITE
                    ? plugin.config().ticksAfterBitten()
                    : plugin.config().ticksAfterCaught())
                .run(() -> PlayerActions.useFishingRod(player));
        }
    }
}
