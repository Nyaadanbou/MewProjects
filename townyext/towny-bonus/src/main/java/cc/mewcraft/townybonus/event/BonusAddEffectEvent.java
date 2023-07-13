package cc.mewcraft.townybonus.event;

import cc.mewcraft.townybonus.object.bonus.Bonus;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class BonusAddEffectEvent extends BonusTriggerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final PotionEffectType effectType;

    public BonusAddEffectEvent(@NotNull Bonus bonus, Player who, PotionEffectType effectType) {
        super(bonus);
        this.player = who;
        this.effectType = effectType;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public PotionEffectType getEffectType() {
        return effectType;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

}
