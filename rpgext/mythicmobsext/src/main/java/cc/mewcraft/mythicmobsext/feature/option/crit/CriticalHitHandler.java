package cc.mewcraft.mythicmobsext.feature.option.crit;

import cc.mewcraft.mythicmobsext.feature.option.PlayerAttackHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.damage.DamageMetadata;
import org.jetbrains.annotations.NotNull;

@Singleton
public class CriticalHitHandler implements PlayerAttackHandler {

    private final @NotNull CriticalHitManager criticalHitManager;

    @Inject
    public CriticalHitHandler(
        final @NotNull CriticalHitManager criticalHitManager
    ) {
        this.criticalHitManager = criticalHitManager;
    }

    @Override
    public void handle(@NotNull PlayerAttackEvent event, @NotNull ActiveMob activeMob) {
        CriticalHitModifiers modifiers = criticalHitManager.getModifiers(activeMob.getType());
        if (modifiers == null) return;

        DamageMetadata damageMeta = event.getDamage();

        // Vanilla critical hits
        if (modifiers.hasType(CriticalHitType.VANILLA) && CriticalUtils.isVanillaCriticalHit(event)) {
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
