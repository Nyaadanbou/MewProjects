package co.mcsky.moecore.economy;

import cat.nyaa.nyaacore.component.ISystemBalance;
import co.mcsky.moecore.MoeCore;
import com.google.common.base.Charsets;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.logging.Level;

/**
 * Implements {@link ISystemBalance} and adds some convenient methods.
 */
public class SystemAccountUtils implements ISystemBalance {

    private final String serverAccount;
    private final World bukkitWorld;

    public SystemAccountUtils() {
        this.serverAccount = TownyEconomyHandler.getServerAccount();
        this.bukkitWorld = Bukkit.getWorlds().get(0);
    }

    public boolean setSystemBalance(double amount) {
        return TownyEconomyHandler.setBalance(serverAccount, amount, bukkitWorld);
    }

    public double getSystemBalance() {
        return TownyEconomyHandler.getBalance(serverAccount, bukkitWorld);
    }

    public boolean depositSystem(double amount) {
        return TownyEconomyHandler.addToServer(amount, bukkitWorld);
    }

    public boolean withdrawSystem(double amount) {
        return TownyEconomyHandler.subtractFromServer(amount, bukkitWorld);
    }

    public boolean depositPlayer(OfflinePlayer payee, double amount) {
        Economy eco = MoeCore.plugin.economy();
        double balanceToDeposit = eco.getBalance(payee);
        try {
            EconomyResponse response = eco.depositPlayer(payee, amount);
            return response.transactionSuccess();
        } catch (Exception e) {
            double balanceToDepositAft = eco.getBalance(payee);
            eco.withdrawPlayer(payee, balanceToDepositAft - balanceToDeposit);
            MoeCore.plugin.getLogger().log(Level.SEVERE, "Error depositing player, rolling back: ", e);
            return false;
        }
    }

    public boolean withdrawPlayer(OfflinePlayer payer, double amount) {
        Economy eco = MoeCore.plugin.economy();
        double balanceToWithdraw = eco.getBalance(payer);
        try {
            EconomyResponse response = eco.withdrawPlayer(payer, amount);
            return response.transactionSuccess();
        } catch (Exception e) {
            double balanceToWithdrawAft = eco.getBalance(payer);
            eco.depositPlayer(payer, balanceToWithdraw - balanceToWithdrawAft);
            MoeCore.plugin.getLogger().log(Level.SEVERE, "Error withdraw player, rolling back: ", e);
            return false;
        }
    }

    /**
     * Withdraws the system account and deposit the money to the player
     *
     * @param offlinePlayer the player to deposit
     * @param amount        the amount of money
     * @return whether the transaction is successful
     */
    public boolean depositFromSystem(OfflinePlayer offlinePlayer, double amount) {
        if (depositPlayer(offlinePlayer, amount)) {
            withdrawSystem(amount);
            return true;
        }
        return false;
    }

    /**
     * Withdraws the player and deposit the money to the system account
     *
     * @param offlinePlayer the player to withdraw
     * @param amount        the amount of money
     * @return whether the transaction is successful
     */
    public boolean withdrawToSystem(OfflinePlayer offlinePlayer, double amount) {
        if (withdrawPlayer(offlinePlayer, amount)) {
            depositSystem(amount);
            return true;
        }
        return false;
    }

    /**
     * Calculates the offline UUID of the offline player name according to Mojang standard.
     *
     * @param name the offline player name
     * @return the offline UUID of the player name
     */
    public UUID getOfflinePlayerUUIDFromName(String name) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
    }

    @Override
    public double getBalance() {
        return getSystemBalance();
    }

    @Override
    public void setBalance(double balance, JavaPlugin operator) {
        setSystemBalance(balance);
    }

    @Override
    public double withdrawAllowDebt(double amount, JavaPlugin operator) {
        withdrawSystem(amount);
        return getBalance();
    }

    @Override
    public double deposit(double amount, JavaPlugin operator) {
        depositSystem(amount);
        return getBalance();
    }
}
