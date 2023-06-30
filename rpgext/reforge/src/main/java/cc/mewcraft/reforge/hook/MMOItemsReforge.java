package cc.mewcraft.reforge.hook;

import cc.mewcraft.reforge.ReforgePlugin;
import cc.mewcraft.reforge.api.Reforge;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.Indyuce.mmoitems.api.ReforgeOptions;
import net.Indyuce.mmoitems.api.util.MMOItemReforger;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Following is the order of options from the source code of MMOItems:
 * <ol>
 *     <li>keepName</li>
 *     <li>keepLore</li>
 *     <li>keepEnchantments</li>
 *     <li>keepUpgrades</li>
 *     <li>keepGemStones</li>
 *     <li>keepSoulBind</li>
 *     <li>keepExternalSH</li>
 *     <li>reRoll</li>
 *     <li>keepModifications</li>
 *     <li>keepAdvancedEnchantments</li>
 *     <li>keepSkins</li>
 *     <li>KeepTier</li>
 * </ol>
 * <p>
 * The option data format should be a list of numbers separated by commas, e.g., "1,2,3,4".
 * That a number is present means that the corresponding option should be true.
 */
@Singleton
public class MMOItemsReforge implements Reforge {
    private final ReforgePlugin plugin;
    private final MMOItemsReforgeOption option;

    @Inject
    public MMOItemsReforge(final ReforgePlugin plugin, final MMOItemsReforgeOption option) {
        this.plugin = plugin;
        this.option = option;
    }

    @Override public Optional<ItemStack> transform(final @NotNull ItemStack item, final @NotNull String optionKey) {
        ReforgeOptions options = option.parse(optionKey);
        if (options == null) {
            plugin.getSLF4JLogger().error("Unknown option key: {}", optionKey);
            return Optional.empty();
        }

        MMOItemReforger mod = new MMOItemReforger(item);
        if (!mod.hasTemplate()) {
            plugin.getSLF4JLogger().error("Failed to reforge item due to null template: {}", optionKey);
            return Optional.empty();
        }
        if (!mod.reforge(options)) {
            plugin.getSLF4JLogger().error("Failed to reforge item: {}", optionKey);
            return Optional.empty();
        }

        return Optional.ofNullable(mod.getResult());
    }
}
