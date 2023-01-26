package co.mcsky.mewcore.economy;

import co.mcsky.mewcore.MewCore;
import me.lucko.helper.Services;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class VaultChecker {

    public static void registerVaultEconomy() {
        // Just indicates the loading status of Vault.
        Services.get(Economy.class).ifPresent(eco -> MewCore.logger().info("Vault economy is hooked"));
    }

    public static void registerVaultPermission() {
        // Just indicates the loading status of Vault economy.
        Services.get(Permission.class).ifPresent(eco -> MewCore.logger().info("Vault permission is hooked"));
    }

    public static void registerVaultChat() {
        // Just indicates the loading status of Vault economy.
        Services.get(Chat.class).ifPresent(eco -> MewCore.logger().info("Vault chat is hooked"));
    }
}
