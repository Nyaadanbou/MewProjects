package co.mcsky.moecore;

import co.mcsky.moecore.economy.SystemAccount;
import co.mcsky.moecore.hook.NyaaCoreHook;
import co.mcsky.moecore.hook.VaultHook;
import me.lucko.helper.Helper;
import me.lucko.helper.plugin.ExtendedJavaPlugin;

public class MoeCore extends ExtendedJavaPlugin {

    public static MoeCore plugin;

    protected MoeConfig config;
    protected SystemAccount systemAccount;

    public static boolean loaded(String name) {
        return Helper.plugins().getPlugin(name) != null;
    }

    public static void log(String info) {
        MoeCore.plugin.getLogger().info(info);
    }

    @Override
    protected void enable() {
        plugin = this;

        if (loaded("Vault")) {
            VaultHook.registerVaultChat();
            VaultHook.registerVaultEconomy();
            VaultHook.registerVaultPermission();
        }

        if (loaded("NyaaCore")) {
            NyaaCoreHook.registerNyaaComponent();
        }

        this.config = new MoeConfig();
        this.config.load();
        this.config.save();
    }

    @Override
    protected void disable() {

    }

    /**
     * Returns the instance of {@link SystemAccount}.
     *
     * @return the SystemAccount instance
     */
    public SystemAccount systemAccount() {
        if (systemAccount == null) {
            throw new IllegalStateException("not implemented");
        }
        return systemAccount;
    }

    /**
     * Sets the instance of {@link SystemAccount}.
     *
     * @param systemAccount the system account instance
     */
    public void systemAccount(SystemAccount systemAccount) {
        this.systemAccount = systemAccount;
    }

    /**
     * @return true if debug mode is on, otherwise false
     */
    public boolean debugMode() {
        return config.debug;
    }

}
