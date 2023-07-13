package cc.mewcraft.townybonus.event;

import cc.mewcraft.townybonus.object.bonus.Bonus;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BonusModifyUpkeepNationEvent extends BonusTriggerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final double modified;

    public BonusModifyUpkeepNationEvent(@NotNull Bonus bonus, double modified) {
        super(bonus);
        this.modified = modified;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public double getModifiedUpkeep() {
        return modified;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

}
