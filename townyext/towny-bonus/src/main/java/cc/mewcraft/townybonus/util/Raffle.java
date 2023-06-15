package cc.mewcraft.townybonus.util;

import me.lucko.helper.random.VariableAmount;

public final class Raffle {

    private final int range;
    private final int number;

    private Raffle(int range, int number) {
        this.range = range;
        this.number = number;
    }

    public static Raffle of(int range, int number) {
        return new Raffle(range, number);
    }

    /**
     * Returns whether this test is passed.
     * <p>
     * The probability of this test follows a uniform distribution, and the
     * probability of this test being passed is {@code number / range}.
     *
     * @return true if the test is passed
     */
    public boolean test() {
        return VariableAmount.baseWithRandomAddition(0, range + 1).getFlooredAmount() <= number;
    }

}
