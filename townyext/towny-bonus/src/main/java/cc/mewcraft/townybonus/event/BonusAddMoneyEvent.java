package cc.mewcraft.townybonus.event;

import cc.mewcraft.townybonus.object.bonus.Bonus;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BonusAddMoneyEvent extends BonusTriggerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final boolean raffle2;
    private final double finalMoney;

    public BonusAddMoneyEvent(@NotNull Bonus bonus, @NotNull Player who, double finalMoney, boolean jackpot) {
        super(bonus);
        this.player = who;
        this.raffle2 = jackpot;
        this.finalMoney = finalMoney;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public double getFinalMoney() {
        return finalMoney;
    }

    public boolean isJackpot() {
        return raffle2;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

}
