package co.mcsky.moecore.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Proxy of {@code SystemAccountImpl}. Client should use this class to manipulate with the economy.
 */
public class SystemAccount {

    private final SystemAccountImpl systemAccount;

    public SystemAccount() {
        systemAccount = new SystemAccountImpl();
    }

    public SystemAccountImpl getImpl() {
        return systemAccount;
    }

    public boolean setSystemBalance(double amount) {
        return systemAccount.setSystemBalance(amount);
    }

    public double getSystemBalance() {
        return systemAccount.getSystemBalance();
    }

    public String getSystemBalanceString(int scale) {
        return BigDecimal.valueOf(getSystemBalance()).setScale(3, RoundingMode.HALF_UP).toPlainString();
    }

    public boolean depositSystem(double amount) {
        return systemAccount.depositSystem(amount);
    }

    public boolean withdrawSystem(double amount) {
        return systemAccount.withdrawSystem(amount);
    }

    public boolean depositPlayer(OfflinePlayer payee, double amount) {
        return systemAccount.depositPlayer(payee, amount);
    }

    public boolean withdrawPlayer(OfflinePlayer payer, double amount) {
        return systemAccount.withdrawPlayer(payer, amount);
    }

    /**
     * Withdraws the system account and deposit the money to the player
     *
     * @param offlinePlayer the player to deposit
     * @param amount        the amount of money
     * @return whether the transaction is successful
     */
    public boolean depositFromSystem(OfflinePlayer offlinePlayer, double amount) {
        return systemAccount.depositFromSystem(offlinePlayer, amount);
    }

    /**
     * Withdraws the player and deposit the money to the system account
     *
     * @param offlinePlayer the player to withdraw
     * @param amount        the amount of money
     * @return whether the transaction is successful
     */
    public boolean withdrawToSystem(OfflinePlayer offlinePlayer, double amount) {
        return systemAccount.withdrawToSystem(offlinePlayer, amount);
    }

    public double getBalance() {
        return systemAccount.getBalance();
    }

    public void setBalance(double balance, JavaPlugin operator) {
        systemAccount.setBalance(balance, operator);
    }

    public double withdrawAllowDebt(double amount, JavaPlugin operator) {
        return systemAccount.withdrawAllowDebt(amount, operator);
    }

    public double deposit(double amount, JavaPlugin operator) {
        return systemAccount.deposit(amount, operator);
    }
}
