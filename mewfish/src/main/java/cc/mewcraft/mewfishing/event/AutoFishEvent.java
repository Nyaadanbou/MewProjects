package cc.mewcraft.mewfishing.event;

import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class AutoFishEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final FishHook fishHook;
    private boolean cancelled;

    public AutoFishEvent(Player who, FishHook fishHook) {
        super(who);
        this.fishHook = fishHook;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public FishHook getHook() {
        return fishHook;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
