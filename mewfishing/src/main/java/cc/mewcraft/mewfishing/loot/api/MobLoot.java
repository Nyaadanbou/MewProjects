package cc.mewcraft.mewfishing.loot.api;

import cc.mewcraft.mewfishing.event.FishLootEvent;
import org.bukkit.entity.Mob;
import org.bukkit.event.player.PlayerFishEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public interface MobLoot extends Loot {
    Mob getEntity(FishLootEvent event);

    @Override default void apply(FishLootEvent event) {
        PlayerFishEvent fishEvent = event.getFishEvent();
    }
}
