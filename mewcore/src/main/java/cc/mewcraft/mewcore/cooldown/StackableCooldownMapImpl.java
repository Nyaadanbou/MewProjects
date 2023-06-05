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

public class StackableCooldownMapImpl<T> implements StackableCooldownMap<T> {

    private final Cooldown base;
    private final LoadingCache<T, StackableCooldown> cache;

    StackableCooldownMapImpl(Cooldown base, Function<T, Integer> charge) {
        this.base = base;
        this.cache = CacheBuilder.newBuilder()
                // remove from the cache 1000 times of charge time after accessing
                .expireAfterAccess(base.getTimeout() * 1000L, TimeUnit.MILLISECONDS)
                .build(new CacheLoader<>() {
                    @Override
                    public @NotNull StackableCooldown load(@NotNull T key) {
                        return StackableCooldown.of(base, key, charge);
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
