package co.mcsky.moecore.hook;

import co.mcsky.moecore.Message;
import co.mcsky.moecore.MoeCore;
import me.lucko.helper.Services;
import me.lucko.helper.utils.Players;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class VaultHook {

    /**
     * Just indicates the loading status of Vault.
     */
    public static void registerVaultEconomy() {
        Services.get(Economy.class).ifPresent(eco -> MoeCore.log("Vault economy is hooked"));
    }

    /**
     * Just indicates the loading status of Vault economy.
     */
    public static void registerVaultPermission() {
        Services.get(Permission.class).ifPresent(eco -> MoeCore.log("Vault permission is hooked"));
    }

    /**
     * Just indicates the loading status of Vault economy.
     */
    public static void registerVaultChat() {
        Services.get(Chat.class).ifPresent(eco -> MoeCore.log("Vault chat is hooked"));
        final Component component = Message.of("Hello {player}!")
                .replace("player", Players.getNullable(""))
                .replace("item", new ItemStack(Material.ACACIA_BOAT))
                .replace("price", 3.1231231D)
                .asComponent();
    }
}
