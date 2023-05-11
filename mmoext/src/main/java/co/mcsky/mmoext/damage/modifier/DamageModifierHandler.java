package co.mcsky.mmoext.damage.modifier;

import co.mcsky.mmoext.damage.PlayerAttackHandler;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This handler modifies the incoming damage to MM mobs.
 */
public class DamageModifierHandler implements PlayerAttackHandler {

    @Override
    public void handle(@NotNull PlayerAttackEvent event, @NotNull ActiveMob activeMob) {
        DamageModifiers damageModifiers = SDamageModifierManager.getInstance().getModifiers(activeMob.getType());
        if (damageModifiers == null) return;

        damageModifiers.forEach(mod -> {
            event.getAttack().getDamage().multiplicativeModifier(mod.getCoefficient(), mod.getDamageType());
        });
    }

}
