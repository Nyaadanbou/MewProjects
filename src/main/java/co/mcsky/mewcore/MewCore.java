package co.mcsky.mewcore;

import co.mcsky.mewcore.economy.SystemAccount;
import co.mcsky.mewcore.hook.VaultHook;
import co.mcsky.mewcore.item.PluginItemRegistry;
import co.mcsky.mewcore.item.impl.BreweryHook;
import co.mcsky.mewcore.item.impl.InteractiveBooksHook;
import co.mcsky.mewcore.item.impl.ItemsAdderHook;
import co.mcsky.mewcore.item.impl.MMOItemsHook;
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

        if (isPluginPresent("ItemsAdder"))
            PluginItemRegistry.registerForConfig("itemsadder", ItemsAdderHook::new);
        if (isPluginPresent("MMOItems"))
            PluginItemRegistry.registerForConfig("mmoitems", MMOItemsHook::new);
        if (isPluginPresent("Brewery"))
            PluginItemRegistry.registerForConfig("brewery", BreweryHook::new);
        if (isPluginPresent("InteractiveBooks"))
            PluginItemRegistry.registerForConfig("interactivebooks", InteractiveBooksHook::new);
    }

    @Override
    protected void disable() {

    }

}
