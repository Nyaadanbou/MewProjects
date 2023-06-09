package cc.mewcraft.adventurelevel.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class AdventurePlayerLevelChangeEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;

    public AdventurePlayerLevelChangeEvent(final @NotNull Player who) {
        super(who);
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override public boolean isCancelled() {
        return cancelled;
    }

    @Override public void setCancelled(final boolean cancel) {
        cancelled = cancel;
    }
}
