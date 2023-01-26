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
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Logger;

public class MewCore extends ExtendedJavaPlugin {

    public static MewCore plugin;

    private PluginItemRegistry pluginItemRegistry;

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

        pluginItemRegistry = new PluginItemRegistry(this);
        pluginItemRegistry.registerForConfig("itemsadder", () -> new ItemsAdderItem(this));
        pluginItemRegistry.registerForConfig("mmoitems", () -> new MMOItemsItem(this));
        pluginItemRegistry.registerForConfig("brewery", () -> new BreweryItem(this));
        pluginItemRegistry.registerForConfig("interactivebooks", () -> new InteractiveBooksItem(this));
    }

    public @NotNull PluginItemRegistry getPluginItemRegistry() {
        return Objects.requireNonNull(pluginItemRegistry);
    }

}
