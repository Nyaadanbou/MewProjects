package cc.mewcraft.mewfishing.module.fishpower;

import cc.mewcraft.mewcore.cooldown.StackableCooldown;
import cc.mewcraft.mewcore.progressbar.ProgressbarDisplay;
import cc.mewcraft.mewcore.progressbar.ProgressbarGenerator;
import cc.mewcraft.mewcore.util.UtilTowny;
import cc.mewcraft.mewfishing.MewFishing;
import cc.mewcraft.mewfishing.MewPerms;
import cc.mewcraft.mewfishing.event.AutoFishEvent;
import me.lucko.helper.Events;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataKey;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class FishPowerModule implements TerminableModule {

    private final MetadataKey<Cooldown> KEY_HELP_COOLDOWN = MetadataKey.createCooldownKey("help_cooldown");
    private final MetadataKey<Cooldown> KEY_WARN_COOLDOWN = MetadataKey.createCooldownKey("warn_cooldown");
    private final int WARN_INTERVAL_SEC = 1;
    private final int HELP_INTERVAL_SEC = 300;

    private final MewFishing plugin;
    private final CooldownManager cooldownManager;
    private final CooldownMessenger cooldownMessenger;
    private final ProgressbarDisplay progressbarDisplay;

    public FishPowerModule(final MewFishing plugin) {
        this.plugin = plugin;

        this.cooldownManager = new CooldownManager(plugin);
        this.cooldownMessenger = new CooldownMessenger(plugin, cooldownManager);

        this.progressbarDisplay = new ProgressbarDisplay(
            plugin.config().progressbarStayTime(),
            ProgressbarGenerator.Builder.builder()
                .left(plugin.lang().of("cooldown_progressbar.left").plain())
                .full(plugin.lang().of("cooldown_progressbar.full").plain())
                .empty(plugin.lang().of("cooldown_progressbar.empty").plain())
                .right(plugin.lang().of("cooldown_progressbar.right").plain())
                .width(plugin.config().progressbarWidth())
                .build()
        );
    }

    public @NotNull CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        if (!plugin.config().fishPowerEnabled()) {
            plugin.log("FishingPower", false);
            return;
        }

        // register a messenger to sync cooldown across servers
        plugin.registerListener(cooldownMessenger).bindWith(consumer);

        // listen on player fishing - it's the entry point of this module
        Events.subscribe(PlayerFishEvent.class, EventPriority.HIGHEST)
            .filter(e -> !e.getPlayer().hasPermission(MewPerms.PERM_ADMIN))
            .filter(e -> UtilTowny.isInWilderness(e.getHook().getLocation()))
            .handler(this::handleFishing).bindWith(consumer);

        // show progress info about cooldown stacks when toggling sneak
        Events.subscribe(PlayerToggleSneakEvent.class)
            .filter(e -> e.getPlayer().getFishHook() != null)
            .filter(e -> UtilTowny.isInWilderness(e.getPlayer().getLocation()))
            .handler(e -> showProgressbar(e.getPlayer())).bindWith(consumer);

        // cancel auto fishing if cooldown stacks are run out
        Events.subscribe(AutoFishEvent.class)
            .filter(e -> !cooldownManager.getData(e.getPlayer().getUniqueId()).testSilently())
            .filter(e -> UtilTowny.isInWilderness(e.getHook().getLocation()))
            .handler(e -> e.setCancelled(true)).bindWith(consumer);
    }

    private void handleFishing(PlayerFishEvent event) {
        Player player = event.getPlayer();

        showProgressbar(player); // show progressbar to the player

        if (Metadata.provideForPlayer(player).getOrPut(KEY_HELP_COOLDOWN, () -> Cooldown.of(HELP_INTERVAL_SEC, TimeUnit.SECONDS)).test()) {
            plugin.lang().of("msg_how_to_check_status").send(player); // tell the player how to check power status
        }

        if (EnumSet.of(State.CAUGHT_FISH, State.REEL_IN, State.BITE).contains(event.getState())) { // only count caught
            Entity caught = event.getCaught();
            if (!(caught instanceof Item item)) { // ignore living entities
                return;
            }

            for (String free : plugin.config().freeItems()) { // hooking free items cost no power
                if (free.contains(item.getItemStack().getType().name().toLowerCase())) {
                    return;
                }
            }

            StackableCooldown cooldown = cooldownManager.getData(player.getUniqueId());
            if (!cooldown.test()) { // the player has no usable stacks
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
            () -> plugin.lang().of("cooldown_progressbar.head").replace("remaining", cooldown.remainingTime(TimeUnit.SECONDS)).replace("amount", cooldown.getAvailable()).plain(),
            () -> plugin.lang().of("cooldown_progressbar.tail").replace("remaining", cooldown.remainingTime(TimeUnit.SECONDS)).replace("amount", cooldown.getAvailable()).plain()
        );
    }

}
