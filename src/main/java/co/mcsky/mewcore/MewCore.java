package co.mcsky.mewcore;

import co.mcsky.mewcore.economy.SystemAccount;
import co.mcsky.mewcore.hook.VaultHook;
import me.lucko.helper.plugin.ExtendedJavaPlugin;

import java.util.logging.Logger;

public class MewCore extends ExtendedJavaPlugin {

    public static MewCore plugin;

    private SystemAccount systemAccount;

    public static Logger logger() {
        return plugin.getLogger();
    }

    /**
     * Returns the instance of {@link SystemAccount}.
     *
     * @return the SystemAccount instance
     */
    public static SystemAccount systemAccount() {
        if (plugin.systemAccount == null)
            throw new IllegalStateException("not implemented");
        return plugin.systemAccount;
    }

    /**
     * Sets the instance of {@link SystemAccount}.
     *
     * @param systemAccount the system account instance
     */
    public static void systemAccount(SystemAccount systemAccount) {
        plugin.systemAccount = systemAccount;
    }

    @Override
    protected void enable() {
        plugin = this;

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            VaultHook.registerVaultChat();
            VaultHook.registerVaultEconomy();
            VaultHook.registerVaultPermission();
        }
    }

    @Override
    protected void disable() {

    }

}
