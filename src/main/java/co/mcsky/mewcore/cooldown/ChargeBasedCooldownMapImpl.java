package co.mcsky.mewcore.cooldown;

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

public class ChargeBasedCooldownMapImpl<T> implements ChargeBasedCooldownMap<T> {

    private final Cooldown base;
    private final LoadingCache<T, ChargeBasedCooldown> cache;

    ChargeBasedCooldownMapImpl(Cooldown base, Function<T, Integer> charge) {
        this.base = base;
        this.cache = CacheBuilder.newBuilder()
                // remove from the cache 1000 times of charge time after accessing
                .expireAfterAccess(base.getTimeout() * 1000L, TimeUnit.MILLISECONDS)
                .build(new CacheLoader<>() {
                    @Override
                    public @NotNull ChargeBasedCooldown load(@NotNull T key) {
                        return ChargeBasedCooldown.of(base, key, charge);
                    }
                });
    }

    @Override
    public @NotNull Cooldown getBase() {
        return base;
    }

    @Override
    public @NotNull ChargeBasedCooldown get(@NotNull T key) {
        return cache.getUnchecked(key);
    }

    @Override
    public void put(@NotNull T key, @NotNull ChargeBasedCooldown cooldown) {
        Objects.requireNonNull(key, "key");
        Preconditions.checkArgument(cooldown.getBaseTimeout() == this.base.getTimeout(), "different timeout");
        this.cache.put(key, cooldown);
    }

    @Override
    public @NotNull Map<T, ChargeBasedCooldown> getAll() {
        return cache.asMap();
    }
}
