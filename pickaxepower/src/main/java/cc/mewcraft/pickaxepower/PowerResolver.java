package cc.mewcraft.pickaxepower;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This class provides methods to resolve pickaxe power from various sources.
 */
public interface PowerResolver {

    /**
     * Resolves the power of specific pickaxe.
     * <p>
     * This will return 0 if the item is not specified in the config.
     *
     * @param item the item to be resolved
     *
     * @return the power
     */
    int resolve(@NotNull ItemStack item);

    /**
     * Resolves the power of specific block.
     * <p>
     * This will return 0 if the block is not specified in the config.
     *
     * @param block the block to be resolved
     *
     * @return current pickaxe power
     */
    int resolve(@NotNull Block block);
}
