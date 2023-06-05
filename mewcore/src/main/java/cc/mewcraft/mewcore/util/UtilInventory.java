package cc.mewcraft.mewcore.util;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;

public final class UtilInventory {

    private UtilInventory() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the item in the specific slot from player inventory.
     * <p>
     * The param slot accepts both {@link EquipmentSlot} name and slot index.
     *
     * @param inv      the player inventory
     * @param slot     the slot to get the item
     * @param fallback fallback item if given slot has no item
     *
     * @return the item stack in the specific slot, or the fallback if there is no item in the specific slot
     */
    public static @Nullable ItemStack getItemInSlot(@NotNull PlayerInventory inv, @NotNull String slot, @Nullable ItemStack fallback) {
        try {
            EquipmentSlot parsed = EquipmentSlot.valueOf(slot.toUpperCase(Locale.ROOT));
            ItemStack itemInSlot = switch (parsed) {
                // held items could be AIR (not null)
                case HAND -> inv.getItemInMainHand();
                case OFF_HAND -> inv.getItemInOffHand();
                // armor items could be null
                case HEAD -> inv.getHelmet();
                case CHEST -> inv.getChestplate();
                case LEGS -> inv.getLeggings();
                case FEET -> inv.getBoots();
            };
            return (itemInSlot == null || itemInSlot.getType().isAir()) ? fallback : itemInSlot.clone();
        } catch (IllegalArgumentException e) { // not a valid equipment slot string - fallback to item slot index
            try {
                int slotIdx = Integer.parseInt(slot);
                return Optional.ofNullable(inv.getItem(slotIdx))
                    .map(ItemStack::clone).orElse(fallback);
            } catch (NumberFormatException ignored) {
                return fallback;
            }
        }
    }

}
