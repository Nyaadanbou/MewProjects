package cc.mewcraft.mythicmobsext.listener;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.mythicmobsext.feature.option.PlayerAttackHandler;
import cc.mewcraft.mythicmobsext.feature.option.crit.CriticalHitHandler;
import cc.mewcraft.mythicmobsext.feature.option.defense.DefenseHandler;
import cc.mewcraft.mythicmobsext.feature.option.display.DamageDisplayHandler;
import cc.mewcraft.mythicmobsext.feature.option.modifier.DamageModifierHandler;
import com.google.inject.Inject;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class PlayerAttackListeners implements AutoCloseableListener {

    private final List<PlayerAttackHandler> handlers;

    @Inject
    public PlayerAttackListeners(
        @NotNull DefenseHandler defenseHandler,
        @NotNull CriticalHitHandler criticalHitHandler,
        @NotNull DamageModifierHandler damageModifierHandler,
        @NotNull DamageDisplayHandler damageDisplayHandler
    ) {
        handlers = new LinkedList<>();
        handlers.add(defenseHandler);
        handlers.add(criticalHitHandler);
        handlers.add(damageModifierHandler);
        handlers.add(damageDisplayHandler); // Damage indicator must be the last to run
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAttack(PlayerAttackEvent event) {
        MythicBukkit.inst()
            .getMobManager()
            .getActiveMob(event.getEntity().getUniqueId())
            .ifPresent(mob -> {
                for (PlayerAttackHandler handler : handlers) {
                    handler.handle(event, mob);
                }
            });
    }

}