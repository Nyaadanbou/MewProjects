package co.mcsky.moecore;

import co.mcsky.moecore.economy.SystemAccount;
import co.mcsky.moecore.hook.VaultHook;
import me.lucko.helper.Helper;
import me.lucko.helper.plugin.ExtendedJavaPlugin;

import java.util.logging.Logger;

public class MoeCore extends ExtendedJavaPlugin {

    public static MoeCore plugin;

    private MoeConfig config;
    private SystemAccount systemAccount;

    public static boolean loaded(String name) {
        return Helper.plugins().getPlugin(name) != null;
    }

    public static Logger logger() {
        return plugin.getLogger();
    }

    public static MoeConfig config() {
        return plugin.config;
    }

    /**
     * Returns the instance of {@link SystemAccount}.
     *
     * @return the SystemAccount instance
     */
    public static SystemAccount systemAccount() {
        if (plugin.systemAccount == null) {
            throw new IllegalStateException("not implemented");
        }
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

        if (loaded("Vault")) {
            VaultHook.registerVaultChat();
            VaultHook.registerVaultEconomy();
            VaultHook.registerVaultPermission();
        }

        this.config = new MoeConfig();
        this.config.load();
        this.config.save();
    }

    @Override
    protected void disable() {

    }

}
