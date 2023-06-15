package cc.mewcraft.townybonus.listener;

import cc.mewcraft.townybonus.object.culture.CultureLevel;
import cc.mewcraft.townybonus.TownyBonus;
import cc.mewcraft.townybonus.event.BonusAddEffectEvent;
import cc.mewcraft.townybonus.event.BonusAddMoneyEvent;
import cc.mewcraft.townybonus.event.BonusModifyExpEvent;
import cc.mewcraft.townybonus.object.bonus.Bonus;
import cc.mewcraft.townybonus.object.bonus.BonusExp;
import cc.mewcraft.townybonus.object.bonus.BonusMoney;
import cc.mewcraft.townybonus.object.bonus.BonusPotionEffect;
import cc.mewcraft.townybonus.util.UtilCulture;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class EntityDeathListener implements TerminableModule {

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        Events.subscribe(EntityDeathEvent.class).handler(e -> {

            if (!(e.getEntity() instanceof Monster)) return;

            Player p = e.getEntity().getKiller();
            if (p == null) return;

            final Optional<CultureLevel> cultureLevel = UtilCulture.cultureLevelOf(p);
            if (cultureLevel.isEmpty()) return;

            final List<Bonus> bonusList = cultureLevel.get().getBonusList();
            for (Bonus bonus : bonusList) {
                switch (bonus.getType()) {
                    case MONEY -> {
                        BonusMoney moneyBonus = (BonusMoney) bonus;
                        final BonusMoney.Roll guess = BonusMoney.Roll.guess(moneyBonus);
                        if (guess.getRaffle1Outcome()) {
                            // got raffle 1 passed

                            if (guess.getRaffle2Outcome()) {
                                // got raffle 2 passed

                                TownyBonus.p.eco.depositPlayer(p, guess.getJackpotMoney());
                                Events.call(new BonusAddMoneyEvent(moneyBonus, p, guess.getJackpotMoney(), true));
                                TownyBonus.debug(p.getName() + " got jackpot money: " + guess.getJackpotMoney());
                                return;
                            }

                            TownyBonus.p.eco.depositPlayer(p, guess.getBaseMoney());
                            Events.call(new BonusAddMoneyEvent(moneyBonus, p, guess.getBaseMoney(), false));
                            TownyBonus.debug(p.getName() + " got base money: " + guess.getBaseMoney());
                        }
                    }
                    case EXP -> {
                        BonusExp expBonus = (BonusExp) bonus;
                        final int droppedExp = e.getDroppedExp();
                        final int modified = expBonus.modify(droppedExp);
                        if (modified != droppedExp) {
                            e.setDroppedExp(modified);
                            TownyBonus.debug(p.getName() + " got bonus exp: " + modified);
                            Events.call(new BonusModifyExpEvent(expBonus, p, droppedExp, modified));
                        }
                    }
                    case POTION_EFFECT -> {
                        BonusPotionEffect effectBonus = (BonusPotionEffect) bonus;
                        final List<PotionEffect> roll = effectBonus.roll();
                        if (!roll.isEmpty()) {
                            for (PotionEffect potionEffect : roll) {
                                p.addPotionEffect(potionEffect);
                                Events.call(new BonusAddEffectEvent(effectBonus, p, potionEffect.getType()));
                                TownyBonus.debug("%s got bonus effect: %s (amp: %s, dur: %s)".formatted(
                                        p.getName(), potionEffect.getType().getName().toLowerCase(), potionEffect.getAmplifier(), potionEffect.getDuration()));
                            }
                        }
                    }
                }
            }

        }).bindWith(consumer);
    }

}
