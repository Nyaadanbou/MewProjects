package co.mcsky.mmoext.listener;

import co.mcsky.mmoext.Main;
import co.mcsky.mmoext.damage.defense.DefenseManager;
import co.mcsky.mmoext.damage.indicator.DamageIndicatorManager;
import co.mcsky.mmoext.damage.crit.CriticalHitManager;
import co.mcsky.mmoext.damage.modifier.SDamageModifierManager;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicReloadedEvent;
import me.lucko.helper.terminable.Terminable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class MythicMobListener implements Listener, Terminable {

    public MythicMobListener() {
        Main.inst().registerListener(this);
    }

    @EventHandler
    public void onMythicMobsReloaded(MythicReloadedEvent event) {
        // Register MMO damage indicator
        DamageIndicatorManager.getInstance().unregisterAll();
        DamageIndicatorManager.getInstance().registerAll();

        // Register MMO damage modifiers
        SDamageModifierManager.getInstance().unregisterAll();
        SDamageModifierManager.getInstance().registerAll();

        // Register MMO critical hit modifiers
        CriticalHitManager.getInstance().unregisterAll();
        CriticalHitManager.getInstance().registerAll();

        // Register MMO Armor
        DefenseManager.getInstance().unregisterAll();
        DefenseManager.getInstance().registerAll();
    }

    @EventHandler
    public void onConditionLoad(MythicConditionLoadEvent event) {

    }

    @EventHandler
    public void onMechanicLoad(MythicMechanicLoadEvent event) {

    }

    @Override
    public void close() {
        HandlerList.unregisterAll(this);
    }

}
