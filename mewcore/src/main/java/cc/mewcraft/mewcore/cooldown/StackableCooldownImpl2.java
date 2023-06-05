package cc.mewcraft.mewcore.cooldown;

import me.lucko.helper.cooldown.Cooldown;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class StackableCooldownImpl2 implements StackableCooldown {

    private final Cooldown base;
    private final Supplier<Long> stacks;

    StackableCooldownImpl2(@NotNull Cooldown base, @NotNull Supplier<Long> stacks) {
        this.base = base.copy();
        this.stacks = stacks;
    }

    @Override public Cooldown getBase() {
        return base;
    }

    @Override public long getStacks() {
        return stacks.get();
    }

}
