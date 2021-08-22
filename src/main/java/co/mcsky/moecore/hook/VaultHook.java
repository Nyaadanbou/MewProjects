package co.mcsky.moecore.hook;

import co.mcsky.moecore.MoeCore;
import me.lucko.helper.Services;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class VaultHook {

    /**
     * Just indicates the loading status of Vault.
     */
    public static void registerVaultEconomy() {
        Services.get(Economy.class).ifPresent(eco -> MoeCore.logger().info("Vault economy is hooked"));
    }

    /**
     * Just indicates the loading status of Vault economy.
     */
    public static void registerVaultPermission() {
        Services.get(Permission.class).ifPresent(eco -> MoeCore.logger().info("Vault permission is hooked"));
    }

    /**
     * Just indicates the loading status of Vault economy.
     */
    public static void registerVaultChat() {
        Services.get(Chat.class).ifPresent(eco -> MoeCore.logger().info("Vault chat is hooked"));
    }
}
