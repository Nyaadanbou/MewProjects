package co.mcsky.mmoext.listener;

import co.mcsky.mmoext.RPGBridge;
import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.bukkit.BukkitAdapter;
import me.lucko.helper.Schedulers;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataKey;
import me.lucko.helper.terminable.Terminable;
import me.lucko.helper.utils.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class SummonListener implements Listener, Terminable {

    private static final MetadataKey<Cooldown> KEY_ANTI_CLICK_SPAM = MetadataKey.createCooldownKey("anti-click-spam");
    private static final MetadataKey<Cooldown> KEY_ANTI_CHAT_SPAM = MetadataKey.createCooldownKey("anti-chat-spam");

    public SummonListener() {
        RPGBridge.inst().registerListener(this);
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        var player = event.getPlayer();
        var item = event.getItem();
        var cs = CustomStack.byItemStack(item);
        if (cs == null) {
            return; // Not an ItemsAdder item
        }
        var summonItem = RPGBridge.config().getSummonItem(cs.getNamespacedID());
        if (summonItem.isEmpty()) {
            return; // Is an ItemsAdder item but not matching config
        }

        // Anti click spam
        var cooldown = Metadata.provideForPlayer(player).getOrPut(KEY_ANTI_CLICK_SPAM, () -> Cooldown.of(2, TimeUnit.SECONDS));
        if (!cooldown.test()) {
            cooldown.reset();
            event.setCancelled(true);
            if (Metadata.provideForPlayer(player).getOrPut(KEY_ANTI_CHAT_SPAM, () -> Cooldown.of(2, TimeUnit.SECONDS)).test()) {
                player.sendMessage(RPGBridge.lang().getMiniMessage(player, "summon.click_spam"));
            }
            return;
        }

        // Check conditions
        var loc = event.getPlayer().getLocation();
        var conditions = summonItem.get().getCondition();
        if (!conditions.testCooldownSilently(player.getUniqueId(), summonItem.get().getItemId())) {
            player.sendMessage(RPGBridge.lang().getMiniMessage(player, "summon.cooldown_remaining", "sec", String.valueOf(conditions.cooldownRemaining(player.getUniqueId(), summonItem.get().getItemId()))));
            event.setCancelled(true);
            return;
        }
        if (!conditions.testWorld(player.getWorld().getName())) {
            player.sendMessage(RPGBridge.lang().getMiniMessage(player, "summon.world_not_allowed"));
            event.setCancelled(true);
            return;
        }
        if (!conditions.testBiome(loc.getBlock().getBiome())) {
            player.sendMessage(RPGBridge.lang().getMiniMessage(player, "summon.biome_not_allowed"));
            event.setCancelled(true);
            return;
        }
        if (!conditions.testHeight(loc.getY())) {
            player.sendMessage(RPGBridge.lang().getMiniMessage(player, "summon.height_not_allowed"));
            event.setCancelled(true);
            return;
        }
        if (!conditions.testWilderness(loc)) {
            player.sendMessage(RPGBridge.lang().getMiniMessage(player, "summon.not_in_wilderness"));
            event.setCancelled(true);
            return;
        }
        if (!conditions.testNearbyActiveMobs(loc, summonItem.get().getMobId())) {
            player.sendMessage(RPGBridge.lang().getMiniMessage(player, "summon.nearby_same_mob"));
            event.setCancelled(true);
            return;
        }
        if (!conditions.testOpenSpace(loc)) {
            player.sendMessage(RPGBridge.lang().getMiniMessage(player, "summon.space_not_enough"));
            event.setCancelled(true);
            return;
        }

        // Double check MM mob validity
        var boss = MythicProvider.get().getMobManager().getMythicMob(summonItem.get().getMobId());
        if (boss.isEmpty()) {
            // This should never happen, but in case...
            Log.severe("Cannot find mob with ID: " + summonItem.get().getMobId());
            player.sendMessage(RPGBridge.lang().getMiniMessage(player, "summon.fatal_error"));
            event.setCancelled(true);
            return;
        }

        // Reset summon cooldown
        conditions.testCooldown(player.getUniqueId(), summonItem.get().getItemId());
        // Consume one item
        item.subtract();

        var effects = summonItem.get().getEffect();

        // Apply explosion effect
        effects.getExplosion().ifPresent(config -> {
            var intervalTicks = 20;
            Schedulers.sync().runRepeating(task -> {
                if (task.getTimesRan() > config.delay() / intervalTicks) {
                    // This is the final explosion, creating the real one
                    loc.getWorld().createExplosion(loc, config.power());
                    task.stop();
                    return;
                }
                // Create fake explosion before the real one is coming out
                loc.getWorld().createExplosion(loc.add(Vector.getRandom().multiply(5)), 0.5F);
            }, 20, intervalTicks);
        });
        // Apply sound effect
        effects.getSounds().forEach(config -> {
            Schedulers.sync().runLater(() -> {
                loc.getWorld().playSound(config.sound());
            }, config.delay());
        });
        // Apply potion effect
        effects.getPotions().forEach(config -> {
            Collection<Player> nearbyPlayers = loc.getNearbyPlayers(config.radius());
            nearbyPlayers.forEach(p -> p.addPotionEffect(config.effect()));
        });

        // Delay mob spawning
        Schedulers.sync().runLater(() -> {
            boss.get().spawn(
                    BukkitAdapter.adapt(loc),
                    summonItem.get().getMobLevel(),
                    SpawnReason.SUMMON
            );
        }, summonItem.get().getDelaySpawn());

    }

    @Override
    public void close() {
        HandlerList.unregisterAll(this);
    }

}
