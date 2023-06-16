package cc.mewcraft.mewfishing.util;

import cc.mewcraft.nms.MewNMSProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public final class PlayerActions {
    /**
     * Make specific player use fishing rod.
     */
    public static void useFishingRod(@NotNull Player player) {
        EquipmentSlot hand = null;

        PlayerInventory inv = player.getInventory();
        if (inv.getItemInMainHand().getType() == Material.FISHING_ROD) {
            hand = EquipmentSlot.HAND;
        } else if (inv.getItemInOffHand().getType() == Material.FISHING_ROD) {
            hand = EquipmentSlot.OFF_HAND;
        }

        if (hand != null) {
            MewNMSProvider.get().useItem(player, hand);
            player.swingHand(hand);
        }
    }

    private PlayerActions() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}
