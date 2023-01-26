package co.mcsky.mewcore.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public interface SystemBalance {
    /**
     * Gets the system balance.
     */
    double getBalance();

    /**
     * Force sets the system balance.
     *
     * @param balance  new balance
     * @param operator the plugin initiated this transaction
     */
    void setBalance(double balance, @Nullable JavaPlugin operator);

    /**
     * Withdraws money from the system balance, allowing the remaining amount to be negative.
     *
     * @param amount   how much money should be withdrawn
     * @param operator the plugin initiated this transaction
     *
     * @return the system balance after the transaction
     */
    double withdrawAllowDebt(double amount, @Nullable JavaPlugin operator);

    /**
     * Deposits money into system balance.
     *
     * @param amount   how much money should be deposited
     * @param operator the plugin initiated this transaction
     *
     * @return the system balance after the transaction
     */
    double deposit(double amount, @Nullable JavaPlugin operator);

    boolean depositFromSystem(OfflinePlayer offlinePlayer, double amount);

    boolean withdrawToSystem(OfflinePlayer offlinePlayer, double amount);

    /**
     * Checks if the system balance has enough amount of money.
     *
     * @param amount amount of money
     *
     * @return true if system balance has that much of money, false otherwise
     */
    default boolean hasEnoughBalance(double amount) {
        return getBalance() >= amount;
    }

    default double withdrawAllowDebt(double amount) {
        return withdrawAllowDebt(amount, null);
    }

    default double withdraw(double amount, @Nullable JavaPlugin operator) {
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
