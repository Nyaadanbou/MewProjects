package cc.mewcraft.mewcore.economy;

import cc.mewcraft.mewcore.MewCore;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import me.lucko.helper.Services;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * An implementation using Towny Server Account as the backed system account.
 */
public class TownySystemBalance implements SystemBalance {

    private final Economy economy;
    private final String serverAccount;
    private final World bukkitWorld;

    public TownySystemBalance() {
        serverAccount = TownyEconomyHandler.getServerAccount();
        bukkitWorld = Bukkit.getWorlds().get(0);
        economy = Services.get(Economy.class).orElseThrow(() -> new IllegalStateException("Vault economy is not loaded"));
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
        double balanceToDeposit = economy.getBalance(payee);
        try {
            EconomyResponse response = economy.depositPlayer(payee, amount);
            return response.transactionSuccess();
        } catch (Exception e) {
            double balanceToDepositAft = economy.getBalance(payee);
            economy.withdrawPlayer(payee, balanceToDepositAft - balanceToDeposit);
            MewCore.plugin.getLogger().log(Level.SEVERE, "Error depositing player, rolling back: ", e);
            return false;
        }
    }

    public boolean withdrawPlayer(OfflinePlayer payer, double amount) {
        double balanceToWithdraw = economy.getBalance(payer);
        try {
            EconomyResponse response = economy.withdrawPlayer(payer, amount);
            return response.transactionSuccess();
        } catch (Exception e) {
            double balanceToWithdrawAft = economy.getBalance(payer);
            economy.depositPlayer(payer, balanceToWithdraw - balanceToWithdrawAft);
            MewCore.plugin.getLogger().log(Level.SEVERE, "Error withdraw player, rolling back: ", e);
            return false;
        }
    }

    /**
     * Withdraws the system account and deposit the money to the player
     *
     * @param offlinePlayer the player to deposit
     * @param amount        the amount of money
     *
     * @return whether the transaction is successful
     */
    @Override
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
     *
     * @return whether the transaction is successful
     */
    @Override
    public boolean withdrawToSystem(OfflinePlayer offlinePlayer, double amount) {
        if (withdrawPlayer(offlinePlayer, amount)) {
            depositSystem(amount);
            return true;
        }
        return false;
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
