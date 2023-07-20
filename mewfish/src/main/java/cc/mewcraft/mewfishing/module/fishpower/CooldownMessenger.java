package cc.mewcraft.mewfishing.module.fishpower;

import cc.mewcraft.mewcore.cooldown.StackableCooldown;
import cc.mewcraft.mewfishing.MewFish;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.lucko.helper.Schedulers;
import me.lucko.helper.messaging.Channel;
import me.lucko.helper.messaging.Messenger;
import me.lucko.helper.messaging.util.MappedChannelReceiver;
import me.lucko.helper.terminable.Terminable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalLong;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * This class synchronize cooldown upon join/quit across servers.
 */
@Singleton
public class CooldownMessenger implements Listener, Terminable {

    private final MewFish plugin;
    private final Channel<CooldownMessage> channel;
    private final MappedChannelReceiver<CooldownMessage, UUID, Long> store;
    private final CooldownManager cooldownManager;

    private record CooldownMessage(@NotNull UUID uuid, long lastTested) {}

    @Inject CooldownMessenger(
        final @NotNull MewFish plugin,
        final @NotNull CooldownManager cooldownManager
    ) {
        this.plugin = plugin;
        this.cooldownManager = cooldownManager;

        this.channel = plugin.getService(Messenger.class).getChannel("mewfishing-cooldown-sync", CooldownMessage.class);

        this.store = MappedChannelReceiver.createExpiring(
            channel,
            CooldownMessage::uuid,
            CooldownMessage::lastTested,
            10,
            TimeUnit.SECONDS
        );
    }

    void sendData(@NotNull UUID uuid, long lastTested) {
        channel.sendMessage(new CooldownMessage(uuid, lastTested));
    }

    @NotNull OptionalLong getData(@NotNull UUID uuid) {
        Long value = store.getValue(uuid);
        return value == null ? OptionalLong.empty() : OptionalLong.of(value);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        StackableCooldown cooldown = cooldownManager.getData(player.getUniqueId());
        if (cooldown.getLastTested().isPresent()) {
            // Only if the lastTested was set, do we send the message (though, it rarely happens)
            sendData(player.getUniqueId(), cooldown.getLastTested().getAsLong());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent event) {
        // If the message store has the data stored,
        // then set the lastTested for the cooldown.

        Schedulers.sync().runLater(() -> {
            Player player = event.getPlayer();
            StackableCooldown cooldown = cooldownManager.getData(player.getUniqueId());
            getData(player.getUniqueId()).ifPresent(cooldown::setLastTested);
        }, plugin.config().networkLatency(), TimeUnit.MILLISECONDS);

        // NB: delay a while to allow the message to be spread
    }

    @Override public void close() {
        store.close();
        HandlerList.unregisterAll(this);
    }

}
