package cc.mewcraft.pickaxepower;

import cc.mewcraft.mewcore.util.PDCUtils;
import cc.mewcraft.mewcore.util.UtilComponent;
import com.google.inject.Inject;
import me.lucko.helper.function.chain.Chain;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

public class LoreWriterImpl implements LoreWriter {
    private static final NamespacedKey LORE_SIZE_KEY = new NamespacedKey("pickaxepower", "lore_size");

    private final PickaxePower plugin;
    private final PowerResolver powerResolver;

    @Inject
    public LoreWriterImpl(
        @NonNull PickaxePower plugin,
        @NonNull PowerResolver powerResolver
    ) {
        this.plugin = plugin;
        this.powerResolver = powerResolver;
    }

    @Override
    public ItemStack update(@Nullable final ItemStack item) {
        if (item == null || item.getType().isAir() || !Tag.ITEMS_PICKAXES.isTagged(item.getType()))
            return item;

        int pickaxePower = powerResolver.resolve(item);
        Component powerText = Chain.start(plugin.getConfig().getString("item.pickaxe-power-lore"))
            .map(s -> UtilComponent.asComponent(s, Placeholder.component("power", text(pickaxePower))))
            .end().orElse(empty());

        ItemMeta itemMeta = Objects.requireNonNull(item.getItemMeta());
        List<Component> lore = itemMeta.hasLore() ? Objects.requireNonNull(itemMeta.lore()) : new ArrayList<>();
        lore.add(0, powerText);
        PDCUtils.set(itemMeta, LORE_SIZE_KEY, 1);
        itemMeta.lore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    public ItemStack revert(@Nullable final ItemStack item) {
        if (item == null || item.getType().isAir() || !Tag.ITEMS_PICKAXES.isTagged(item.getType()))
            return item;

        ItemMeta itemMeta = Objects.requireNonNull(item.getItemMeta());
        Optional<Integer> size = PDCUtils.getInt(itemMeta, LORE_SIZE_KEY);
        if (size.isEmpty())
            return item;

        List<Component> lore = itemMeta.hasLore() ? Objects.requireNonNull(itemMeta.lore()) : new ArrayList<>();
        lore.remove(0);
        itemMeta.lore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
}
