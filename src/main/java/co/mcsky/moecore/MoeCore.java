package co.mcsky.moecore;

import cat.nyaa.nyaacore.component.ISystemBalance;
import cat.nyaa.nyaacore.component.NyaaComponent;
import co.mcsky.moecore.economy.SystemAccount;
import me.lucko.helper.Services;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;

public class MoeCore extends ExtendedJavaPlugin {

    public static MoeCore plugin;

    private MoeConfig config;
    private Economy economy;
    private LuckPerms luckperms;

    private SystemAccount systemAccount;

    @Override
    protected void enable() {
        plugin = this;

        // load vault services
        try {
            this.economy = Services.load(Economy.class);
            this.luckperms = Services.load(LuckPerms.class);
        } catch (IllegalStateException e) {
            getLogger().severe(e.getMessage());
            getLogger().severe("Some vault registration is not present");
            disable();
            return;
        }

        // after vault is loaded successfully, initialize system account
        this.systemAccount = new SystemAccount();

        // register NyaaCore ISystemBalance component
        // so that all fee functions of NyaaUtils
        // can link to the towny server account
        NyaaComponent.register(ISystemBalance.class, systemAccount.getImpl());

        this.config = new MoeConfig();
        this.config.load();
        this.config.save();
    }

    @Override
    protected void disable() {

    }

    /**
     * @return the Economy instance
     */
    public Economy economy() {
        return economy;
    }

    /**
     * @return the LuckPerms instance
     */
    public LuckPerms luckperms() {
        return luckperms;
    }

    /**
     * @return the SystemAccount instance
     */
    public SystemAccount systemAccount() {
        return systemAccount;
    }

    /**
     * @return true if debug mode is on, otherwise false
     */
    public boolean debugMode() {
        return config.debug;
    }
}
