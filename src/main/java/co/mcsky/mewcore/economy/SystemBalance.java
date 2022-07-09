package co.mcsky.mewcore.economy;

import org.bukkit.plugin.java.JavaPlugin;

public interface SystemBalance {
    /**
     * How much is the system balance
     */
    double getBalance();

    /**
     * Force set the balance
     *
     * @param balance  new balance
     * @param operator (optional) the plugin initiated this transaction. Can be
     *                 null.
     */
    void setBalance(double balance, JavaPlugin operator);

    /**
     * withdraw money from system balance allowing the remaining amount to be
     * negative
     *
     * @param amount   how much money should be withdrawn
     * @param operator (optional) the plugin initiated this transaction. Can be
     *                 null.
     * @return system balance after the transaction
     */
    double withdrawAllowDebt(double amount, JavaPlugin operator);

    /**
     * deposit money into system balance
     *
     * @param amount   how much money should be deposit
     * @param operator (optional) the plugin initiated this transaction. Can be
     *                 null.
     * @return system balance after the transaction
     */
    double deposit(double amount, JavaPlugin operator);

    /**
     * check if the system balance has enough amount of money
     *
     * @param amount amount of money
     * @return true if system balance has that much of money, false otherwise
     */
    default boolean hasEnoughBalance(double amount) {
        return getBalance() >= amount;
    }

    default double withdrawAllowDebt(double amount) {
        return withdrawAllowDebt(amount, null);
    }

    default double withdraw(double amount, JavaPlugin operator) {
        if (!hasEnoughBalance(amount)) throw new IllegalArgumentException();
        return withdrawAllowDebt(amount, operator);
    }

    default double withdraw(double amount) {
        if (!hasEnoughBalance(amount)) throw new IllegalArgumentException();
        return withdrawAllowDebt(amount, null);
    }

    default double deposit(double amount) {
        return deposit(amount, null);
    }
}
