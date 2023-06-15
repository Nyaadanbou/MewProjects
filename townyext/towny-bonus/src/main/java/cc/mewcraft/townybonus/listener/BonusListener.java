package cc.mewcraft.townybonus.listener;

import cc.mewcraft.townybonus.TownyBonus;
import cc.mewcraft.townybonus.event.*;
import cc.mewcraft.townybonus.object.bonus.NoticeType;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BonusListener implements TerminableModule {

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {

        Events.subscribe(BonusAddMoneyEvent.class).handler(e -> {
            final Player p = e.getPlayer();
            for (NoticeType opt : e.getBonus().getNoticeOptions()) {
                switch (opt) {
                    case ACTION_BAR -> {
                        final Component actionbar = TownyBonus.lang().getMiniMessage(p, "bonus.money.actionbar", "amount", String.valueOf(e.getFinalMoney()));
                        p.sendActionBar(actionbar);
                    }
                    case TITLE -> {
                        final Component title = TownyBonus.lang().getMiniMessage(p, "bonus.money.title", "amount", String.valueOf(e.getFinalMoney()));
                        final Component subtitle = TownyBonus.lang().getMiniMessage(p, "bonus.money.subtitle", "amount", String.valueOf(e.getFinalMoney()));
                        p.showTitle(Title.title(title, subtitle));
                    }
                    case CHAT -> {
                        final Component chat = TownyBonus.lang().getMiniMessage(p, "bonus.money.chat", "amount", String.valueOf(e.getFinalMoney()));
                        p.sendMessage(chat);
                    }
                }
            }

        }).bindWith(consumer);

        Events.subscribe(BonusAddEffectEvent.class).handler(e -> {
            final Player p = e.getPlayer();

            for (NoticeType opt : e.getBonus().getNoticeOptions()) {
                switch (opt) {
                    case ACTION_BAR -> {
                        final Component actionbar = TownyBonus.lang().getMiniMessage(p, "bonus.effect.actionbar", "type", e.getEffectType().getKey().getKey());
                        p.sendActionBar(actionbar);
                    }
                    case TITLE -> {
                        final Component title = TownyBonus.lang().getMiniMessage(p, "bonus.effect.title", "type", e.getEffectType().getKey().getKey());
                        final Component subtitle = TownyBonus.lang().getMiniMessage(p, "bonus.effect.subtitle", "type", e.getEffectType().getKey().getKey());
                        p.showTitle(Title.title(title, subtitle));
                    }
                    case CHAT -> {
                        final Component chat = TownyBonus.lang().getMiniMessage(p, "bonus.effect.chat", "type", e.getEffectType().getKey().getKey());
                        p.sendMessage(chat);
                    }
                }
            }

        }).bindWith(consumer);

        Events.subscribe(BonusModifyExpEvent.class).handler(e -> {
            final Player p = e.getPlayer();
            for (NoticeType opt : e.getBonus().getNoticeOptions()) {
                switch (opt) {
                    case ACTION_BAR -> {
                        final Component actionbar = TownyBonus.lang().getMiniMessage(p, "bonus.exp.actionbar", "base", String.valueOf(e.getDroppedExp()), "extra", String.valueOf(e.getModifiedExp() - e.getDroppedExp()), "total", String.valueOf(e.getModifiedExp()));
                        p.sendActionBar(actionbar);
                    }
                    case TITLE -> {
                        final Component title = TownyBonus.lang().getMiniMessage(p, "bonus.exp.title", "base", String.valueOf(e.getDroppedExp()), "extra", String.valueOf(e.getModifiedExp() - e.getDroppedExp()), "total", String.valueOf(e.getModifiedExp()));
                        final Component subtitle = TownyBonus.lang().getMiniMessage(p, "bonus.exp.subtitle", "base", String.valueOf(e.getDroppedExp()), "extra", String.valueOf(e.getModifiedExp() - e.getDroppedExp()), "total", String.valueOf(e.getModifiedExp()));
                        p.showTitle(Title.title(title, subtitle));
                    }
                    case CHAT -> {
                        final Component chat = TownyBonus.lang().getMiniMessage(p, "bonus.exp.chat", "base", String.valueOf(e.getDroppedExp()), "extra", String.valueOf(e.getModifiedExp() - e.getDroppedExp()), "total", String.valueOf(e.getModifiedExp()));
                        p.sendMessage(chat);
                    }
                }
            }
        }).bindWith(consumer);

        Events.subscribe(BonusModifyUpkeepNationEvent.class).handler(e -> {
            // nothing to show
        }).bindWith(consumer);

        Events.subscribe(BonusModifyUpkeepTownEvent.class).handler(e -> {
            // nothing to show
        }).bindWith(consumer);

    }

}
