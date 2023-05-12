package cc.mewcraft.mewfishing.loot.impl.loot;

import cc.mewcraft.mewcore.util.UtilComponent;
import cc.mewcraft.mewfishing.event.FishLootEvent;
import cc.mewcraft.mewfishing.loot.api.Conditioned;
import cc.mewcraft.mewfishing.loot.api.ItemLoot;
import cc.mewcraft.mewfishing.util.NumberStylizer;
import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.*;

/**
 * Represents a loot item that has custom name, lore, enchantments and custom model data.
 */
@SuppressWarnings("unused")
@DefaultQualifier(NonNull.class)
public class CustomItemLoot extends AbstractLoot<ItemStack> implements ItemLoot {

    private final Material mat;
    @Nullable private final String name;
    private final List<String> lore;
    private final Map<String, String> enchantments;
    private final int customModelData;

    /**
     * Constructs a custom item without any meta (essentially a SimpleItem).
     *
     * @param weight          the weight
     * @param amount          the amount
     * @param mat             the material
     * @param name            the name, or null if none
     * @param lore            the lore, or empty list if none
     * @param enchantments    the enchantments, or empty map if none
     * @param customModelData the custom model data, or 0 if none
     */
    protected CustomItemLoot(
        double weight,
        String amount,
        List<Conditioned> conditions,
        Material mat,
        @Nullable String name,
        List<String> lore,
        Map<String, String> enchantments,
        int customModelData
    ) {
        super(weight, amount, conditions);
        this.mat = mat;
        this.name = name;
        this.lore = lore;
        this.enchantments = enchantments;
        this.customModelData = customModelData;
    }

    @Override public ItemStack getItem(FishLootEvent event) {
        ItemStack item = new ItemStack(mat);

        // Set amount
        item.setAmount(getAmount());

        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

        // Set display name if any
        meta.displayName(UtilComponent.asComponent(name));

        // Set lore
        meta.lore(lore.isEmpty() ? null : UtilComponent.asComponent(lore));

        // Set enchantments
        enchantments.forEach((e, l) -> {
            int level = NumberStylizer.getStylizedInt(l);
            Enchantment enchantment = Objects.requireNonNull(EnchantmentWrapper.getByKey(NamespacedKey.minecraft(e))); // This should never be null
            if (meta instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
                enchantmentStorageMeta.addStoredEnchant(enchantment, level, true);
            } else {
                item.addUnsafeEnchantment(enchantment, level);
            }
        });

        // Set custom model data
        meta.setCustomModelData(customModelData == 0 ? null : customModelData);

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Gets the display name of this item if any, or {@code null} if none.
     * <p>
     * Note that color codes in the string are {@link ChatColor#COLOR_CHAR}.
     *
     * @return the display name of this item if any, or {@code null} if none
     */
    public @Nullable String getName() {
        return name;
    }

    /**
     * Gets the lore on this item, or empty list if none.
     * <p>
     * Note that color codes in the string are {@link ChatColor#COLOR_CHAR}.
     *
     * @return the lore on this item, or empty list if none
     */
    public List<String> getLore() {
        return lore;
    }

    /**
     * @return the enchantments on this item, or empty list if none
     */
    public Map<String, String> getEnchantments() {
        return enchantments;
    }

    /**
     * @return the custom model data on this item
     */
    public Optional<Integer> getCustomModelData() {
        return customModelData == 0 ? Optional.empty() : Optional.of(customModelData);
    }

    @SuppressWarnings({"unused", "UnusedReturnValue"})
    public static class CustomLootItemBuilder {
        private double weight;
        private String amount;
        private List<Conditioned> conditions;
        private final Material mat;
        @Nullable private String name;
        private final List<String> lore;
        private final Map<String, String> enchantments;
        private int customModelData;

        public CustomLootItemBuilder(String type) {
            Preconditions.checkNotNull(type);

            weight = 1;
            amount = "1";
            conditions = Collections.emptyList();
            mat = Objects.requireNonNull(Material.matchMaterial(type), "Unknown Material Type: " + type);
            name = null;
            lore = new ArrayList<>();
            enchantments = new HashMap<>();
            customModelData = 0; // 0 means no custom model data
        }

        public CustomLootItemBuilder weight(double weight) {
            this.weight = weight;
            return this;
        }

        public CustomLootItemBuilder amount(@Nullable String amount) {
            if (amount == null) return this;
            this.amount = amount;
            return this;
        }

        public CustomLootItemBuilder conditions(@Nullable List<Conditioned> conditions) {
            if (conditions == null) return this;
            this.conditions = conditions;
            return this;
        }

        /**
         * Set the display name of this item.
         *
         * @param name the display name to set
         */
        public CustomLootItemBuilder name(@Nullable String name) {
            if (name != null)
                this.name = name;
            return this;
        }

        /**
         * Add lore to this item.
         *
         * @param lore the lore to add
         */
        public CustomLootItemBuilder lore(@Nullable List<String> lore) {
            if (lore == null || lore.isEmpty()) return this;
            this.lore.addAll(lore);
            return this;
        }

        /**
         * @param lore the lore to add
         *
         * @see #lore(List)
         */
        public CustomLootItemBuilder lore(@Nullable String... lore) {
            return lore(Arrays.asList(lore));
        }

        /**
         * Add enchantment to this item.
         *
         * @param key   the namespaced key of the enchantment. See <a
         *              href="https://minecraft.fandom.com/wiki/Enchanting">Enchanting (Minecraft Wiki)</a> for the
         *              correct namespaced key (click on any enchantment page from the table, then you can see the
         *              namespaced key)
         * @param level the level of this enchantment. Stylised number is supported, see {@link NumberStylizer} for
         *              details
         */
        public CustomLootItemBuilder enchantment(String key, String level) {
            if (EnchantmentWrapper.getByKey(NamespacedKey.minecraft(key)) == null) {
                throw new NullPointerException("Invalid enchantment key: " + key);
            }
            this.enchantments.put(key.trim().toLowerCase(), level);
            return this;
        }

        /**
         * Set the custom model data on this item. Passing {@code 0} to clear the custom model data.
         *
         * @param customModelData the custom model data to set, or {@code 0} to clear it
         */
        public CustomLootItemBuilder customModelData(int customModelData) {
            this.customModelData = customModelData;
            return this;
        }

        public CustomItemLoot build() {
            return new CustomItemLoot(
                weight,
                amount,
                conditions,
                mat,
                name,
                lore,
                enchantments,
                customModelData
            );
        }
    }

}
