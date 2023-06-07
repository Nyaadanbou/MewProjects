package cc.mewcraft.mythicmobsext.feature.option.modifier;

import cc.mewcraft.mythicmobsext.feature.option.PlayerAttackHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This handler modifies the incoming damage to MM mobs.
 */
@Singleton
public class DamageModifierHandler implements PlayerAttackHandler {

    private final @NotNull DamageModifierManager damageModifierManager;

    @Inject
    public DamageModifierHandler(final @NotNull DamageModifierManager damageModifierManager) {
        this.damageModifierManager = damageModifierManager;
    }

    @Override
    public void handle(@NotNull PlayerAttackEvent event, @NotNull ActiveMob activeMob) {
        DamageModifiers modifiers = damageModifierManager.getModifiers(activeMob.getType());
        if (modifiers == null) {
            return;
        }

        for (DamageModifier mod : modifiers) {
            event.getAttack().getDamage().multiplicativeModifier(mod.getCoefficient(), mod.getDamageType());
        }
    }

}
