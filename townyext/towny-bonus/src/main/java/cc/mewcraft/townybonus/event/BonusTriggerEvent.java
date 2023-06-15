package cc.mewcraft.townybonus.event;

import cc.mewcraft.townybonus.object.bonus.Bonus;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BonusTriggerEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Bonus bonus;

    public BonusTriggerEvent(@NotNull Bonus bonus) {
        this.bonus = bonus;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public @NotNull Bonus getBonus() {
        return bonus;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

}
