package co.mcsky.mewcore.cooldown;

import me.lucko.helper.cooldown.Cooldown;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * A self-populating map of charge-based cooldown instances
 *
 * @param <T> the type
 */
public interface ChargeBasedCooldownMap<T> {

    @NotNull
    static <T> ChargeBasedCooldownMap<T> create(@NotNull Cooldown base, @NotNull Function<T, Integer> charge) {
        Objects.requireNonNull(base, "base");
        return new ChargeBasedCooldownMapImpl<>(base, charge);
    }

    /**
     * Gets the base charge-based cooldown
     *
     * @return the base charge-based cooldown
     */
    @NotNull
    Cooldown getBase();

    /**
     * Gets the internal charge-based cooldown instance associated with the
     * given key.
     *
     * <p>The inline Cooldown methods in this class should be used to access
     * the functionality of the charge-based cooldown as opposed to calling the
     * methods directly via the instance returned by this method.</p>
     *
     * @param key the key
     * @return a charge-based cooldown instance
     */
    @NotNull
    ChargeBasedCooldown get(@NotNull T key);

    void put(@NotNull T key, @NotNull ChargeBasedCooldown cooldown);

    /**
     * Gets the charge-based cooldowns contained within this collection.
     *
     * @return the backing map
     */
    @NotNull
    Map<T, ChargeBasedCooldown> getAll();

    /* methods from ChargeBasedCooldown */

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

    default long remainingMillisFull(@NotNull T key) {
        return get(key).remainingMillisFull();
    }

    default long remainingTime(@NotNull T key, @NotNull TimeUnit unit) {
        return get(key).remainingTime(unit);
    }

    default long remainingTimeFull(@NotNull T key, @NotNull TimeUnit unit) {
        return get(key).remainingTimeFull(unit);
    }

    @NotNull
    default OptionalLong getLastTested(@NotNull T key) {
        return get(key).getLastTested();
    }

    default void setLastTested(@NotNull T key, long time) {
        get(key).setLastTested(time);
    }
}
