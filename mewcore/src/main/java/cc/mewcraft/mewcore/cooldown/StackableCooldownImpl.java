package cc.mewcraft.mewcore.cooldown;

import me.lucko.helper.cooldown.Cooldown;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class StackableCooldownImpl<T> implements StackableCooldown {

    private final T key;
    private final Cooldown base;
    private final Function<T, Integer> maxCharge;

    StackableCooldownImpl(@NotNull Cooldown base, @NotNull T key, @NotNull Function<T, Integer> charge) {
        this.base = base.copy();

        this.key = key;
        this.maxCharge = charge;
    }

    @Override public Cooldown getBase() {
        return base;
    }

    @Override public long getMaximum() {
        return maxCharge.apply(key);
    }

}
