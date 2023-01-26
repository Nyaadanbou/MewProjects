package co.mcsky.mewcore;

import co.mcsky.mewcore.economy.SystemBalance;
import co.mcsky.mewcore.economy.TownySystemBalance;
import co.mcsky.mewcore.economy.VaultChecker;
import co.mcsky.mewcore.item.api.PluginItemRegistry;
import co.mcsky.mewcore.item.impl.BreweryItem;
import co.mcsky.mewcore.item.impl.InteractiveBooksItem;
import co.mcsky.mewcore.item.impl.ItemsAdderItem;
import co.mcsky.mewcore.item.impl.MMOItemsItem;
import me.lucko.helper.Services;
import me.lucko.helper.plugin.ExtendedJavaPlugin;

import java.util.logging.Logger;

public class MewCore extends ExtendedJavaPlugin {

    public static MewCore plugin;

    public static Logger logger() {
        return plugin.getLogger();
    }

    @Override
    protected void disable() {}

    @Override
    protected void enable() {
        plugin = this;

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            VaultChecker.registerVaultChat();
            VaultChecker.registerVaultEconomy();
            VaultChecker.registerVaultPermission();

            Services.provide(SystemBalance.class, new TownySystemBalance());
        }

        PluginItemRegistry.init(this);
        PluginItemRegistry.get().registerForConfig("itemsadder", () -> new ItemsAdderItem(this));
        PluginItemRegistry.get().registerForConfig("mmoitems", () -> new MMOItemsItem(this));
        PluginItemRegistry.get().registerForConfig("brewery", () -> new BreweryItem(this));
        PluginItemRegistry.get().registerForConfig("interactivebooks", () -> new InteractiveBooksItem(this));
    }

}
