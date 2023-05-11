package co.mcsky.mmoext.listener;

import co.mcsky.mmoext.Main;
import co.mcsky.mmoext.damage.PlayerAttackHandler;
import co.mcsky.mmoext.damage.defense.DefenseHandler;
import co.mcsky.mmoext.damage.indicator.DamageIndicatorHandler;
import co.mcsky.mmoext.damage.crit.CriticalHitHandler;
import co.mcsky.mmoext.damage.modifier.DamageModifierHandler;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import me.lucko.helper.terminable.Terminable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerAttackListener implements Listener, Terminable {

    private final List<PlayerAttackHandler> handlers;

    public PlayerAttackListener() {
        handlers = new ArrayList<>();
        handlers.add(new DefenseHandler());
        handlers.add(new CriticalHitHandler());
        handlers.add(new DamageModifierHandler());
        handlers.add(new DamageIndicatorHandler()); // Damage indicator must be the last to run
        Main.inst().registerListener(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAttack(PlayerAttackEvent event) {
        Optional<ActiveMob> activeMob = MythicBukkit.inst().getMobManager().getActiveMob(event.getEntity().getUniqueId());
        if (activeMob.isPresent()) {
            for (PlayerAttackHandler handler : handlers) {
                handler.handle(event, activeMob.get());
            }
        }
    }

    @Override
    public void close() {
        HandlerList.unregisterAll(this);
    }

}