package co.mcsky.mmoext.damage.crit;

import co.mcsky.mmoext.damage.PlayerAttackHandler;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.damage.DamageMetadata;
import org.jetbrains.annotations.NotNull;

public class CriticalHitHandler implements PlayerAttackHandler {

    @Override
    public void handle(@NotNull PlayerAttackEvent event, @NotNull ActiveMob activeMob) {
        CriticalHitModifiers modifiers = CriticalHitManager.getInstance().getModifiers(activeMob.getType());
        if (modifiers == null) return;

        DamageMetadata damageMeta = event.getDamage();

        // Vanilla critical hits
        if (modifiers.hasType(CriticalHitType.VANILLA) && CriticalHitManager.isVanillaCriticalHit(event)) {
            damageMeta.multiplicativeModifier(modifiers.getType(CriticalHitType.VANILLA));
        }

        // MMO weapon critical hits
        if (modifiers.hasType(CriticalHitType.MMO_WEAPON) && damageMeta.isWeaponCriticalStrike()) {
            damageMeta.multiplicativeModifier(modifiers.getType(CriticalHitType.MMO_WEAPON));
        }

        // MMO skill critical hits
        if (modifiers.hasType(CriticalHitType.MMO_SKILL) && damageMeta.isSkillCriticalStrike()) {
            damageMeta.multiplicativeModifier(modifiers.getType(CriticalHitType.MMO_SKILL));
        }
    }

}
