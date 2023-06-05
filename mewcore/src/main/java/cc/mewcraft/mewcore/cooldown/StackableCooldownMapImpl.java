package cc.mewcraft.mewcore.cooldown;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import me.lucko.helper.cooldown.Cooldown;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public class StackableCooldownMapImpl<T> implements StackableCooldownMap<T> {

    private final Cooldown base;
    private final LoadingCache<T, StackableCooldown> cache;

    StackableCooldownMapImpl(@NotNull Cooldown base, long stacks) {
        this.base = base;
        this.cache = CacheBuilder.newBuilder()
            .expireAfterAccess(base.getTimeout() * 1000L, TimeUnit.MILLISECONDS)
            .build(new CacheLoader<>() {
                @Override
                public @NotNull StackableCooldown load(@NotNull T key) {
                    return StackableCooldown.of(base, stacks);
                }
            });
    }

    StackableCooldownMapImpl(@NotNull Cooldown base, @NotNull Supplier<Long> stacks) {
        this.base = base;
        this.cache = CacheBuilder.newBuilder()
            .expireAfterAccess(base.getTimeout() * 1000L, TimeUnit.MILLISECONDS)
            .build(new CacheLoader<>() {
                @Override
                public @NotNull StackableCooldown load(@NotNull T key) {
                    return StackableCooldown.of(base, stacks);
                }
            });
    }

    StackableCooldownMapImpl(@NotNull Cooldown base, @NotNull Function<T, Long> stacks) {
        this.base = base;
        this.cache = CacheBuilder.newBuilder()
            .expireAfterAccess(base.getTimeout() * 1000L, TimeUnit.MILLISECONDS)
            .build(new CacheLoader<>() {
                @Override
                public @NotNull StackableCooldown load(@NotNull T key) {
                    return StackableCooldown.of(base, key, stacks);
                }
            });
    }

    @Override public @NotNull Cooldown getBase() {
        return base;
    }

    @Override public @NotNull StackableCooldown get(@NotNull T key) {
        return cache.getUnchecked(key);
    }

    @Override public void put(@NotNull T key, @NotNull StackableCooldown cooldown) {
        Objects.requireNonNull(key, "key");
        Preconditions.checkArgument(cooldown.getBaseTimeout() == this.base.getTimeout(), "different timeout");
        this.cache.put(key, cooldown);
    }

    @Override public @NotNull Map<T, StackableCooldown> getAll() {
        return cache.asMap();
    }

}
