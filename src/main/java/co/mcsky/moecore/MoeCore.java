package co.mcsky.moecore;

import cat.nyaa.nyaacore.component.ISystemBalance;
import cat.nyaa.nyaacore.component.NyaaComponent;
import co.mcsky.moecore.economy.SystemAccountUtils;
import me.lucko.helper.Services;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;

public class MoeCore extends ExtendedJavaPlugin {

    public static MoeCore plugin;

    private MoeConfig config;
    private Economy economy;
    private LuckPerms luckperms;

    private SystemAccountUtils systemAccount;

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
        this.systemAccount = new SystemAccountUtils();

        // register NyaaCore ISystemBalance component
        // so that all fee functions of NyaaUtils
        // can link to the towny server account
        NyaaComponent.register(ISystemBalance.class, this.systemAccount);

        this.config = new MoeConfig();
        this.config.load();
        this.config.save();
    }

    @Override
    protected void disable() {

    }

    public Economy economy() {
        return economy;
    }

    public LuckPerms luckperms() {
        return luckperms;
    }

    public SystemAccountUtils systemAccount() {
        return systemAccount;
    }

    public boolean debugMode() {
        return config.debug;
    }
}
