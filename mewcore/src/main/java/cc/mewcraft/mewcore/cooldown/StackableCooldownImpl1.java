package cc.mewcraft.mewcore.cooldown;

import me.lucko.helper.cooldown.Cooldown;
import org.jetbrains.annotations.NotNull;

public class StackableCooldownImpl1 implements StackableCooldown {

    private final Cooldown base;
    private final long stacks;

    StackableCooldownImpl1(@NotNull Cooldown base, long stacks) {
        this.base = base.copy();
        this.stacks = stacks;
    }

    @Override public Cooldown getBase() {
        return base;
    }

    @Override public long getStacks() {
        return stacks;
    }

}
