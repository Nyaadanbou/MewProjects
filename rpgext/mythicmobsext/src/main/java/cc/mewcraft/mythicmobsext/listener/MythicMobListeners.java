package cc.mewcraft.mythicmobsext.listener;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.mythicmobsext.feature.condition.MaxHealthCondition;
import cc.mewcraft.mythicmobsext.feature.option.crit.CriticalHitManager;
import cc.mewcraft.mythicmobsext.feature.option.defense.DefenseManager;
import cc.mewcraft.mythicmobsext.feature.option.display.DamageDisplayManager;
import cc.mewcraft.mythicmobsext.feature.option.modifier.DamageModifierManager;
import com.google.inject.Inject;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.events.MythicReloadedEvent;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class MythicMobListeners implements AutoCloseableListener {

    private final @NotNull DamageDisplayManager damageDisplayManager;
    private final @NotNull DamageModifierManager damageModifierManager;
    private final @NotNull CriticalHitManager criticalHitManager;
    private final @NotNull DefenseManager defenseManager;

    @Inject
    public MythicMobListeners(
        final @NotNull DamageDisplayManager damageDisplayManager,
        final @NotNull DamageModifierManager damageModifierManager,
        final @NotNull CriticalHitManager criticalHitManager,
        final @NotNull DefenseManager defenseManager
    ) {
        this.damageDisplayManager = damageDisplayManager;
        this.damageModifierManager = damageModifierManager;
        this.criticalHitManager = criticalHitManager;
        this.defenseManager = defenseManager;
    }

    @EventHandler
    public void onMythicMobsReloaded(MythicReloadedEvent event) {
        // When MythicMobs has reloaded its config files...

        // Register MMO damage indicator
        damageDisplayManager.unregister();
        damageDisplayManager.register();

        // Register MMO damage modifiers
        damageModifierManager.unregister();
        damageModifierManager.register();

        // Register MMO critical hit modifiers
        criticalHitManager.unregister();
        criticalHitManager.register();

        // Register MMO Armor
        defenseManager.unregister();
        defenseManager.register();
    }

    @EventHandler
    public void onConditionLoad(MythicConditionLoadEvent event) {
        String conditionName = event.getConditionName();

        //noinspection SwitchStatementWithTooFewBranches
        switch (conditionName) {
            case "maxhealth" -> event.register(new MaxHealthCondition(event.getConfig()));
        }
    }

    /*@EventHandler
    public void onMechanicLoad(MythicMechanicLoadEvent event) {

    }*/

}
