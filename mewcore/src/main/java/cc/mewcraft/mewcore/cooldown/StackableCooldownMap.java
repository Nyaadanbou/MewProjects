package cc.mewcraft.mewcore.cooldown;

import me.lucko.helper.cooldown.Cooldown;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A self-populating map of stackable cooldown instances
 *
 * @param <T> the type
 */
public interface StackableCooldownMap<T> {

    static @NotNull <T> StackableCooldownMap<T> create(@NotNull Cooldown base, long stacks) {
        Objects.requireNonNull(base, "base");
        return new StackableCooldownMapImpl<>(base, stacks);
    }

    static @NotNull <T> StackableCooldownMap<T> create(@NotNull Cooldown base, @NotNull Supplier<Long> stacks) {
        Objects.requireNonNull(base, "base");
        return new StackableCooldownMapImpl<>(base, stacks);
    }

    static @NotNull <T> StackableCooldownMap<T> create(@NotNull Cooldown base, @NotNull Function<T, Long> stacks) {
        Objects.requireNonNull(base, "base");
        return new StackableCooldownMapImpl<>(base, stacks);
    }

    /**
     * Gets the base stackable cooldown
     *
     * @return the base stackable cooldown
     */
    @NotNull Cooldown getBase();

    /**
     * Gets the internal stackable cooldown instance associated with the given key.
     *
     * <p>The inline Cooldown methods in this class should be used to access the functionality of the stackable
     * cooldown as opposed to calling the methods directly via the instance returned by this method.
     *
     * @param key the key
     *
     * @return a stackable cooldown instance
     */
    @NotNull StackableCooldown get(@NotNull T key);

    void put(@NotNull T key, @NotNull StackableCooldown cooldown);

    /**
     * Gets the stackable cooldowns contained within this collection.
     *
     * @return the backing map
     */
    @NotNull Map<T, StackableCooldown> getAll();

    /* methods from StackableCooldown */

    default boolean test(@NotNull T key) {
        return get(key).test();
    }

    default boolean testSilently(@NotNull T key) {
        return get(key).testSilently();
    }

    default long elapsed(@NotNull T key) {
        return get(key).elapsed();
    }

    default void consumeOne(@NotNull T key) {
        get(key).consumeOne();
    }

    default void consumeAll(@NotNull T key) {
        get(key).consumeAll();
    }

    default long remainingMillis(@NotNull T key) {
        return get(key).remainingMillis();
    }

    default long remainingMillisAll(@NotNull T key) {
        return get(key).remainingMillisAll();
    }

    default long remainingTime(@NotNull T key, @NotNull TimeUnit unit) {
        return get(key).remainingTime(unit);
    }

    default long remainingTimeAll(@NotNull T key, @NotNull TimeUnit unit) {
        return get(key).remainingTimeAll(unit);
    }

    default @NotNull OptionalLong getLastTested(@NotNull T key) {
        return get(key).getLastTested();
    }

    default void setLastTested(@NotNull T key, long time) {
        get(key).setLastTested(time);
    }

}
