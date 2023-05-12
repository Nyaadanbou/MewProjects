package cc.mewcraft.mewutils.module.color_palette;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;


/**
 * Handle the color palette for {@link ItemFrame}.
 */
public class ItemFramePaletteHandler extends PaletteHandler<ItemFrame> {

    public ItemFramePaletteHandler(final ColorPaletteModule module) {
        super(module);
    }

    @Override
    public @NotNull Color getColor(final @NotNull ItemFrame base) {
        ItemStack item = base.getItem(); // it's a copy
        if (item.getType() == Material.LEATHER_HORSE_ARMOR) {
            if (item.getItemMeta() instanceof LeatherArmorMeta meta)
                return meta.getColor();
        }
        return Color.WHITE;
    }

    @Override
    public void setColor(final @NotNull ItemFrame base, final @NotNull Color color) {
        ItemStack item = base.getItem(); // it's a copy
        item.setType(Material.LEATHER_HORSE_ARMOR);
        item.editMeta(LeatherArmorMeta.class, meta -> meta.setColor(color));
        base.setItem(item);
    }

    @Override
    public boolean canHandle(final @NotNull Entity base) {
        return base instanceof ItemFrame;
    }

}
