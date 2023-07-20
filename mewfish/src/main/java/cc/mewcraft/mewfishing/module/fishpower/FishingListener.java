package cc.mewcraft.mewfishing.module.fishpower;

import cc.mewcraft.mewcore.cooldown.StackableCooldown;
import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.mewcore.progressbar.ProgressbarDisplay;
import cc.mewcraft.mewcore.util.ServerOriginUtils;
import cc.mewcraft.mewfishing.MewFish;
import cc.mewcraft.mewfishing.event.AutoFishEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

@Singleton
public class FishingListener implements AutoCloseableListener {
    private final MetadataKey<Cooldown> KEY_HELP_COOLDOWN = MetadataKey.createCooldownKey("help_cooldown");
    private final MetadataKey<Cooldown> KEY_WARN_COOLDOWN = MetadataKey.createCooldownKey("warn_cooldown");
    private final int WARN_INTERVAL_SEC = 1;
    private final int HELP_INTERVAL_SEC = 300;

    private final MewFish plugin;
    private final CooldownManager cooldownManager;
    private final ProgressbarDisplay progressbarDisplay;

    @Inject
    public FishingListener(
        final MewFish plugin,
        final CooldownManager cooldownManager,
        final ProgressbarDisplay progressbarDisplay
    ) {
        this.plugin = plugin;
        this.cooldownManager = cooldownManager;
        this.progressbarDisplay = progressbarDisplay;
    }

    @EventHandler
    public void onAutoFish(AutoFishEvent event) {
        Player player = event.getPlayer();

        if (ServerOriginUtils.atOrigin(player.getUniqueId())) {
            // At origin server, it does not consume power
            return;
        }

        if (!cooldownManager.getData(player.getUniqueId()).testSilently()) {
            // Cancel auto fishing if cooldown stacks are run out
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (ServerOriginUtils.atOrigin(player.getUniqueId())) {
            // At origin server, it does not consume power
            return;
        }

        if (player.getFishHook() != null) {
            // Show progress info about cooldown stacks when toggling sneak
            showProgressbar(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFish(PlayerFishEvent event) {
        // Listen on player fishing - it's the entry point of this module

        Player player = event.getPlayer();

        if (ServerOriginUtils.atOrigin(player.getUniqueId())) {
            // At origin server, it does not consume power
            return;
        }

        showProgressbar(player); // Show progressbar to the player

        if (Metadata.provideForPlayer(player).getOrPut(KEY_HELP_COOLDOWN, () -> Cooldown.of(HELP_INTERVAL_SEC, TimeUnit.SECONDS)).test()) {
            plugin.lang().of("msg_how_to_check_status").send(player); // tell the player how to check power status
        }

        if (EnumSet.of(
            /* only count caught */
            PlayerFishEvent.State.CAUGHT_FISH,
            PlayerFishEvent.State.REEL_IN,
            PlayerFishEvent.State.BITE
        ).contains(event.getState())) {
            Entity caught = event.getCaught();
            if (!(caught instanceof Item item)) {
                // Ignore living entities
                return;
            }

            for (String free : plugin.config().freeItems()) {
                if (free.contains(item.getItemStack().getType().name().toLowerCase())) {
                    // Cost no power for free items
                    return;
                }
            }

            StackableCooldown cooldown = cooldownManager.getData(player.getUniqueId());
            if (!cooldown.test()) {
                // The player has no usable stacks

                showProgressbar(player);
                event.getHook().remove();
                event.setCancelled(true);
                if (Metadata.provideForPlayer(player).getOrPut(KEY_WARN_COOLDOWN, () -> Cooldown.of(WARN_INTERVAL_SEC, TimeUnit.SECONDS)).test()) {
                    plugin.lang().of("msg_fishing_in_wild").send(player);
                }
            }
        }
    }

    private void showProgressbar(Player player) {
        StackableCooldown cooldown = cooldownManager.getData(player.getUniqueId());
        progressbarDisplay.show(
            player,
            () -> cooldown.elapsedOne() / (float) cooldown.getBaseTimeout(),
            () -> plugin.lang().of("cooldown_progressbar.head")
                .replace("remaining", cooldown.remainingTime(TimeUnit.SECONDS))
                .replace("amount", cooldown.getAvailable())
                .plain(),
            () -> plugin.lang().of("cooldown_progressbar.tail")
                .replace("remaining", cooldown.remainingTime(TimeUnit.SECONDS))
                .replace("amount", cooldown.getAvailable())
                .plain()
        );
    }
}
