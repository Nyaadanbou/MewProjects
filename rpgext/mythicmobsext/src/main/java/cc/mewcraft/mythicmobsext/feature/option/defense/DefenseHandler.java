package cc.mewcraft.mythicmobsext.feature.option.defense;

import cc.mewcraft.mythicmobsext.feature.option.PlayerAttackHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.util.DefenseFormula;
import org.jetbrains.annotations.NotNull;

@Singleton
public class DefenseHandler implements PlayerAttackHandler {

    private final @NotNull DefenseManager defenseManager;

    @Inject
    public DefenseHandler(
        final @NotNull DefenseManager defenseManager
    ) {
        this.defenseManager = defenseManager;
    }

    @Override public void handle(final @NotNull PlayerAttackEvent event, final @NotNull ActiveMob activeMob) {
        double defense = defenseManager.getDefense(activeMob.getType());
        if (defense > 0) {
            double initialDamage = event.getDamage().getDamage();
            if (initialDamage > 0) { // avoid math error
                double ratio = new DefenseFormula(false).getAppliedDamage(defense, initialDamage) / initialDamage;
                event.getDamage().multiplicativeModifier(ratio);
            }
        }
    }

}
