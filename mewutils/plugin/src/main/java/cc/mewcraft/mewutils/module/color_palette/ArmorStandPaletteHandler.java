package cc.mewcraft.mewutils.module.color_palette;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Handle the color palette for {@link ArmorStand}.
 */
public class ArmorStandPaletteHandler extends PaletteHandler<ArmorStand> {

    public ArmorStandPaletteHandler(ColorPaletteModule module) {
        super(module);
    }

    @Override
    public @NotNull Color getColor(final @NotNull ArmorStand base) {
        ItemStack item = base.getItem(EquipmentSlot.HEAD);
        if (item.getType() == Material.LEATHER_HORSE_ARMOR) {
            if (item.getItemMeta() instanceof LeatherArmorMeta meta)
                return meta.getColor();
        }
        return Color.WHITE;
    }

    @Override
    public void setColor(final @NotNull ArmorStand base, final @NotNull Color color) {
        ItemStack item = base.getItem(EquipmentSlot.HEAD);
        item.setType(Material.LEATHER_HORSE_ARMOR);
        item.editMeta(LeatherArmorMeta.class, meta -> meta.setColor(color));
    }

    @Override
    public boolean canHandle(final @NotNull Entity base) {
        return base instanceof ArmorStand;
    }

}
