package cc.mewcraft.mewfishing.loot.api;

import cc.mewcraft.mewfishing.event.FishLootEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

/**
 * Represents loot of {@link ItemStack}.
 */
@DefaultQualifier(NonNull.class)
public interface ItemLoot extends Loot {

    ItemStack getItem(FishLootEvent event);

    @Override default void apply(FishLootEvent event) {
        PlayerFishEvent fishEvent = event.getFishEvent();
        if (fishEvent.getCaught() instanceof Item entityItem) {
            if (event.isModified()) { // don't override the custom loot item; creating one instead
                Vector v0 = event.getPlayer().getLocation().toVector().subtract(fishEvent.getHook().getLocation().toVector());
                Vector v1 = v0.multiply(0.1D).add(new Vector(0D, v0.length() * 0.08D, 0D));
                Item copy = (Item) entityItem.getWorld().spawnEntity(entityItem.getLocation(), EntityType.DROPPED_ITEM);
                copy.setItemStack(getItem(event));
                copy.setVelocity(v1); // make the new item fly to the player
            } else { // this vanilla loot is not changed yet; just change it
                event.setChanged(true); // mark the loot has been changed
                entityItem.setItemStack(getItem(event));
            }
        }
    }

}
