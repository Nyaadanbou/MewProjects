package cc.mewcraft.mewfishing.loot.impl.condition;

import cc.mewcraft.mewfishing.event.FishLootEvent;
import cc.mewcraft.mewfishing.loot.api.Conditioned;
import io.papermc.paper.world.MoonPhase;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

@DefaultQualifier(NonNull.class)
public class MoonPhaseCondition implements Conditioned {
    private final List<MoonPhase> phases;

    public MoonPhaseCondition(List<String> phases) {
        this.phases = phases.stream().map(t -> MoonPhase.valueOf(t.toUpperCase())).toList();
    }

    @Override public boolean evaluate(final FishLootEvent event) {
        if (phases.isEmpty()) {
            return true;
        }

        MoonPhase phase = event.getFishEvent().getHook().getLocation().getWorld().getMoonPhase();
        return phases.stream().anyMatch(t -> t.equals(phase));
    }
}
