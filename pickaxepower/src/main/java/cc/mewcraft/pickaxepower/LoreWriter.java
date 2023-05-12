package cc.mewcraft.pickaxepower;

import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface LoreWriter {

    @Nullable ItemStack update(@Nullable ItemStack item);

    @Nullable ItemStack revert(@Nullable ItemStack item);
}
