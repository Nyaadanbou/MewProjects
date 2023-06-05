package cc.mewcraft.mewfishing.module.autofish;

import cc.mewcraft.mewfishing.MewFishing;
import cc.mewcraft.mewfishing.event.AutoFishEvent;
import cc.mewcraft.mewfishing.nms.player.PlayerAction;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.jetbrains.annotations.NotNull;

public class AutoFishModule implements TerminableModule {

    private final MewFishing plugin;

    public AutoFishModule(final MewFishing plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        if (!plugin.config().autoFishEnabled()) {
            plugin.log("AutoFishing", false);
            return;
        }

        Events.subscribe(PlayerFishEvent.class).handler(this::handle).bindWith(consumer);
    }

    private void handle(PlayerFishEvent event) {
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
                .run(() -> PlayerAction.doRightClick(player));
        }
    }

}
