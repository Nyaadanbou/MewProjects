package co.mcsky.mmoext.damage.defense;

import co.mcsky.mmoext.damage.PlayerAttackHandler;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.util.DefenseFormula;
import org.jetbrains.annotations.NotNull;

public class DefenseHandler implements PlayerAttackHandler {

    public DefenseHandler() {
    }

    @Override public void handle(@NotNull final PlayerAttackEvent event, @NotNull final ActiveMob activeMob) {
        double defense = DefenseManager.getInstance().getDefense(activeMob.getType());
        if (defense > 0) {
            double initialDamage = event.getDamage().getDamage();
            if (initialDamage > 0) { // avoid math error
                double ratio = new DefenseFormula(false).getAppliedDamage(defense, initialDamage) / initialDamage;
                event.getDamage().multiplicativeModifier(ratio);
            }
        }
    }

}
