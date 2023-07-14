package cc.mewcraft.mewfishing.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.NotNull;

/**
 * A wrapper of {@link PlayerFishEvent} that is called when the player <b>is allowed to</b> get a custom loot.
 * <p>
 * This event provides additional information about the states of custom loots.
 */
@DefaultQualifier(NonNull.class)
public class FishLootEvent extends PlayerEvent implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    private final PlayerFishEvent event;
    private boolean cancel = false;
    private boolean modify = false;

    public FishLootEvent(final PlayerFishEvent event) {
        super(event.getPlayer(), Bukkit.isPrimaryThread());
        this.event = event;
    }

    public PlayerFishEvent getFishEvent() {
        return event;
    }

    /**
     * @return true if the loot of this event has been modified; otherwise false
     */
    public boolean isModified() {
        return modify;
    }

    /**
     * @param modify true to mark the loot has been modified; otherwise false
     */
    public void setChanged(final boolean modify) {
        this.modify = modify;
    }

    @Override public boolean isCancelled() {
        return cancel;
    }

    @Override public void setCancelled(final boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
