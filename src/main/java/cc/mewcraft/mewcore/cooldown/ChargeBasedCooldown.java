package cc.mewcraft.mewcore.cooldown;

import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.time.Time;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public interface ChargeBasedCooldown {

    @NotNull
    static <T> ChargeBasedCooldown of(Cooldown base, T key, Function<T, Integer> charge) {
        return new ChargeBasedCooldownImpl<>(base, key, charge);
    }

    /**
     * Gets the time until a single charge will become usable.
     *
     * @param unit the unit to return in
     * @return the time until a charge will be usable
     */
    default long remainingTime(@NotNull TimeUnit unit) {
        return unit.convert(remainingMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * @see #remainingTime(TimeUnit)
     */
    default long remainingMillis() {
        return remainingMillisFull() % getBaseTimeout();
    }

    /**
     * Gets the time until all charges will become usable.
     *
     * <p>If all charges are usable, this method returns <code>0</code>.</p>
     *
     * @param unit the unit to return in
     * @return the time until all charges will be usable
     */
    default long remainingTimeFull(@NotNull TimeUnit unit) {
        return unit.convert(remainingMillisFull(), TimeUnit.MILLISECONDS);
    }

    /**
     * @see #remainingMillisFull()
     */
    default long remainingMillisFull() {
        return getMaximum() * getBaseTimeout() - elapsed();
    }

    /**
     * Returns true if at least a single charge is usable, and then consumes a
     * charge.
     *
     * <p>If there is no charge usable, the timer is <strong>not</strong>
     * set.</p>
     *
     * @return true if at least a single charge is usable
     */
    default boolean test() {
        if (testSilently()) {
            consumeOne();
            return true;
        }
        return false;
    }

    /**
     * Returns true if at least a single charge is usable
     *
     * @return true if at least a single charge is usable
     */
    default boolean testSilently() {
        return getAvailable() > 0;
    }

    /**
     * Consumes one charge, only if there is one.
     */
    default void consumeOne() {
        long charge = getAvailable();
        if (charge > 0) {
            long remainder = elapsed() % getBaseTimeout();
            long diff = (charge - 1) * getBaseTimeout() + remainder;
            setLastTested(Time.nowMillis() - diff);
        }
    }

    /**
     * Consumes all charges, resetting timer to now.
     */
    default void consumeAll() {
        setLastTested(Time.nowMillis());
    }

    /**
     * @see Cooldown#getLastTested()
     */
    default OptionalLong getLastTested() {
        return getBase().getLastTested();
    }

    /**
     * Sets the time in milliseconds when this base cooldown was last tested.
     *
     * <p>Note: this should only be used internally. Use {@link #test()}
     * otherwise.</p>
     *
     * @param time the time
     */
    default void setLastTested(long time) {
        getBase().setLastTested(time);
    }

    /**
     * Returns the elapsed time in milliseconds since the cooldown was last
     * reset, or the <b>total</b> elapsed time for all charges to be usable.
     *
     * @return the elapsed time
     */
    default long elapsed() {
        // if [elapsed] = [max charge] * [base timeout]
        // then it means none of the charge is used yet
        return Math.min(getBase().elapsed(), getMaximum() * getBaseTimeout());
    }

    default long elapsedOne() {
        return elapsed() % getBaseTimeout();
    }

    default long getBaseTimeout() {
        return getBase().getTimeout();
    }

    default long getBaseTimeout(TimeUnit unit) {
        return unit.convert(getBaseTimeout(), TimeUnit.MILLISECONDS);
    }

    /**
     * Returns the amount of currently available charges
     *
     * @return the amount of currently available charges
     */
    default long getAvailable() {
        return elapsed() / getBaseTimeout();
    }

    /**
     * Returns the base {@link Cooldown} instance.
     * <p>
     * <b>Should not be touched externally.</b>
     *
     * @return the base cooldown
     */
    Cooldown getBase();

    /**
     * Returns the maximum number of charges.
     *
     * @return the maximum number of charges
     */
    long getMaximum();

}
