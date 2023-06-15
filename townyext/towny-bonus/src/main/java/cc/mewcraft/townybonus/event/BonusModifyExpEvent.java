package cc.mewcraft.townybonus.event;

import cc.mewcraft.townybonus.object.bonus.Bonus;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BonusModifyExpEvent extends BonusTriggerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final int dropped;
    private final int modified;

    public BonusModifyExpEvent(@NotNull Bonus bonus, @NotNull Player who, int dropped, int modified) {
        super(bonus);
        this.player = who;
        this.dropped = dropped;
        this.modified = modified;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public int getDroppedExp() {
        return dropped;
    }

    public int getModifiedExp() {
        return modified;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

}
