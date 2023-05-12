package cc.mewcraft.pickaxepower;

import com.google.inject.Inject;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public class PowerResolverImpl implements PowerResolver {

    private final @NonNull PickaxePower plugin;
    private @MonotonicNonNull ConfigurationNode pickaxePowerNode;
    private @MonotonicNonNull ConfigurationNode blockPowerNode;

    @Inject
    public PowerResolverImpl(final @NotNull PickaxePower plugin) {
        this.plugin = plugin;

        try {
            // Loads pickaxe power data
            this.pickaxePowerNode = YamlConfigurationLoader.builder()
                .file(plugin.getDataFolder().toPath().resolve("pickaxes.yml").toFile())
                .indent(2).nodeStyle(NodeStyle.BLOCK)
                .build().load();

            // Loads block power data
            this.blockPowerNode = YamlConfigurationLoader.builder()
                .file(plugin.getDataFolder().toPath().resolve("blocks.yml").toFile())
                .indent(2).nodeStyle(NodeStyle.BLOCK)
                .build()
                .load();
        } catch (ConfigurateException e) {
            e.printStackTrace();
            plugin.getSLF4JLogger().error("There is an error in the config file. This plugin will not work at all!");
        }
    }

    @Override
    public int resolve(final @NotNull ItemStack item) {
        if (pickaxePowerNode == null)
            // config error - all pickaxes have 0 power
            return 0;

        if (!Tag.ITEMS_PICKAXES.isTagged(item.getType()))
            // non-pickaxe items have 0 power
            return 0;

        CustomStack customStack = CustomStack.byItemStack(item);
        if (customStack == null) {
            String namespacedId = item.getType().getKey().asString();
            return pickaxePowerNode.node(namespacedId).getInt(0);
        }

        String namespacedID = customStack.getNamespacedID();
        return pickaxePowerNode.node(namespacedID).getInt(0);
    }

    @Override
    public int resolve(@NotNull final Block block) {
        if (blockPowerNode == null)
            // config not loaded correctly - disable all blocks
            return 999;

        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
        if (customBlock == null)
            // all vanilla blocks have zero power as we don't handle them
            return 0;

        String namespacedID = customBlock.getNamespacedID();
        return blockPowerNode.node(namespacedID).getInt(0);
    }
}
