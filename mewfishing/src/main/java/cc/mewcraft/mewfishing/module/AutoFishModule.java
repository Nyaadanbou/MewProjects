package cc.mewcraft.mewfishing.module;

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

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        if (!MewFishing.conf().autoFishingEnabled()) {
            MewFishing.log("AutoFishing", false);
            return;
        }

        Events.subscribe(PlayerFishEvent.class).handler(AutoFishModule::handle).bindWith(consumer);
    }

    private static void handle(PlayerFishEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        if (!player.hasPermission("mewfishing.auto.use"))
            return;
        if (!new AutoFishEvent(player, event.getHook()).callEvent())
            return;

        if (event.getState() == PlayerFishEvent.State.BITE || event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Schedulers.builder()
                .sync()
                .after(event.getState() == PlayerFishEvent.State.BITE
                    ? MewFishing.conf().ticksAfterBitten()
                    : MewFishing.conf().ticksAfterCaught())
                .run(() -> PlayerAction.doRightClick(player));
        }
    }

}
