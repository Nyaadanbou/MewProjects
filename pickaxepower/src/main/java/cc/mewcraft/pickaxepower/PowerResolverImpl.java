package cc.mewcraft.pickaxepower;

import com.google.inject.Inject;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.ChatColor;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PowerResolverImpl implements PowerResolver {

    private final @NotNull PickaxePower plugin;
    private final @Nullable Map<String, Integer> pickaxePowerMap;
    private final @Nullable Map<String, Integer> blockPowerMap;

    @Inject
    public PowerResolverImpl(final @NotNull PickaxePower plugin) {
        this.plugin = plugin;

        this.pickaxePowerMap = Optional
            .ofNullable(plugin.getConfig().getConfigurationSection("pickaxes"))
            .map(sec -> {
                HashMap<String, Integer> map = new HashMap<>();
                sec.getKeys(false).forEach(k -> map.put(k, sec.getInt(k)));
                return map;
            }).orElse(null);

        this.blockPowerMap = Optional
            .ofNullable(plugin.getConfig().getConfigurationSection("blocks"))
            .map(sec -> {
                HashMap<String, Integer> map = new HashMap<>();
                sec.getKeys(false).forEach(k -> map.put(k, sec.getInt(k)));
                return map;
            }).orElse(null);
    }

    @Override
    public PowerData resolve(final @NotNull ItemStack item) {
        if (pickaxePowerMap == null)
            // config error - all pickaxes have 0 power
            return new PowerData(0, item.translationKey());

        if (!Tag.ITEMS_PICKAXES.isTagged(item.getType()))
            // non-pickaxe items have 0 power
            return new PowerData(0, item.translationKey());

        CustomStack customStack = CustomStack.byItemStack(item);
        if (customStack == null) { // resolve vanilla pickaxes
            String namespacedId = item.getType().getKey().asString();
            return new PowerData(
                pickaxePowerMap.getOrDefault(namespacedId, 0),
                item.translationKey()
            );
        }

        // resolve itemsadder pickaxes
        String namespacedID = customStack.getNamespacedID();
        //noinspection deprecation
        return new PowerData(
            pickaxePowerMap.getOrDefault(namespacedID, 0),
            ChatColor.stripColor(customStack.getDisplayName())
        );
    }

    @Override
    public PowerData resolve(final @NotNull Block block) {
        if (blockPowerMap == null)
            // config not loaded correctly - disable all block breaking
            return new PowerData(999, block.translationKey());

        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
        if (customBlock == null) {
            // all vanilla blocks have zero power as we don't handle them
            return new PowerData(0, block.translationKey());
        }

        // it's a custom block, return its power in config
        String namespacedID = customBlock.getNamespacedID();
        //noinspection deprecation
        return new PowerData(
            blockPowerMap.getOrDefault(namespacedID, 0),
            ChatColor.stripColor(customBlock.getDisplayName())
        );
    }

}
