package cc.mewcraft.enchantment.gui.gui;

import cc.mewcraft.enchantment.gui.api.ChargeableUiEnchant;
import cc.mewcraft.enchantment.gui.api.UiEnchant;
import cc.mewcraft.enchantment.gui.api.UiEnchantProvider;
import cc.mewcraft.enchantment.gui.config.EnchantGuiSettings;
import cc.mewcraft.enchantment.gui.util.AdventureUtils;
import cc.mewcraft.enchantment.gui.util.LoreUtils;
import cc.mewcraft.enchantment.gui.util.NumberUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.util.List;
import java.util.function.Function;

@Singleton
public class ItemProviderCache {
    private final LoadingCache<UiEnchant, ItemProvider[]> cache;

    private final EnchantGuiSettings settings;
    private final EnchantTargetTranslator targetTranslator;

    @Inject
    public ItemProviderCache(
        final EnchantGuiSettings settings,
        final EnchantTargetTranslator targetTranslator
    ) {
        this.cache = CacheBuilder.newBuilder().build(new ItemCacheLoader());
        this.settings = settings;
        this.targetTranslator = targetTranslator;
    }

    public ItemProvider[] get(UiEnchant key) {
        return cache.getUnchecked(key);
    }

    private class ItemCacheLoader extends CacheLoader<UiEnchant, ItemProvider[]> {
        @Override public ItemProvider @NotNull [] load(final @NotNull UiEnchant key) throws Exception {
            int index = 0; // array index
            int min = key.minimumLevel();
            int max = key.maximumLevel();

            ItemProvider[] states = new ItemProvider[max];

            for (int level = min; level <= max; level++) {
                String displayName = settings.displayNameFormat().replace("<enchantment_display_name>", key.displayName().get(level));
                List<String> description = key.description().get(level);
                String rarity = key.rarity().name();
                String targets = targetTranslator.join(", ", key.enchantmentTargets());
                String minLevel = String.valueOf(key.minimumLevel());
                String maxLevel = String.valueOf(key.maximumLevel());

                List<String> loreFormat = settings.itemLoreFormat().all();

                // Lore that is common to all enchantments
                LoreUtils.replacePlaceholder("<enchantment_description>", loreFormat, description);
                LoreUtils.replacePlaceholder("<enchantment_rarity>", loreFormat, rarity);
                LoreUtils.replacePlaceholder("<enchantment_target_list>", loreFormat, targets);
                LoreUtils.replacePlaceholder("<enchantment_level_min>", loreFormat, minLevel);
                LoreUtils.replacePlaceholder("<enchantment_level_max>", loreFormat, maxLevel);

                // Lore that describes conflict
                if (!key.conflict().isEmpty()) {
                    Function<List<Key>, String> concatFunc = conflict -> conflict.stream()
                        .map(UiEnchantProvider::getOrThrow)
                        .map(UiEnchant::name)
                        .reduce((e1, e2) -> e1 + ", " + e2)
                        .orElse("");
                    List<String> conflict = Lists.partition(key.conflict(), 3).stream().map(concatFunc).toList();

                    LoreUtils.replacePlaceholder("<conflicts>", loreFormat, settings.itemLoreFormat().conflicts());
                    LoreUtils.replacePlaceholder("<enchantment_conflict_list>", loreFormat, conflict);
                } else {
                    LoreUtils.removePlaceholder("<conflicts>", loreFormat, false);
                }

                // Lore that describes charging
                if (key instanceof ChargeableUiEnchant chargeable) {
                    LoreUtils.replacePlaceholder("<charging>", loreFormat, settings.itemLoreFormat().charging());
                    LoreUtils.replacePlaceholder("<enchantment_charges_fuel_item>", loreFormat, chargeable.fuel());
                    LoreUtils.replacePlaceholder("<enchantment_charges_consume_amount>", loreFormat, NumberUtil.format(chargeable.fuelConsume().get(level)));
                    LoreUtils.replacePlaceholder("<enchantment_charges_recharge_amount>", loreFormat, NumberUtil.format(chargeable.fuelRecharge().get(level)));
                    LoreUtils.replacePlaceholder("<enchantment_charges_max_amount>", loreFormat, NumberUtil.format(chargeable.maximumFuel().get(level)));
                } else {
                    LoreUtils.removePlaceholder("<charging>", loreFormat, false);
                }

                // Lore that describes how to obtain it
                if (loreFormat.contains("<obtaining>")) {
                    LoreUtils.replacePlaceholder("<obtaining>", loreFormat, settings.itemLoreFormat().obtaining());
                    replaceOrRemove("<enchantment_obtain_chance_enchanting>", loreFormat, key.enchantingChance());
                    replaceOrRemove("<enchantment_obtain_chance_villager>", loreFormat, key.villagerTradeChance());
                    replaceOrRemove("<enchantment_obtain_chance_loot_generation>", loreFormat, key.lootGenerationChance());
                    replaceOrRemove("<enchantment_obtain_chance_fishing>", loreFormat, key.fishingChance());
                    replaceOrRemove("<enchantment_obtain_chance_mob_spawning>", loreFormat, key.mobSpawningChance());
                }

                ItemBuilder builder = new ItemBuilder(settings.itemMaterial());
                builder.setDisplayName(AdventureUtils.miniMessage(displayName));
                builder.setLore(loreFormat.stream().map(AdventureUtils::miniMessage).toList());

                states[index++] = new ItemWrapper(builder.get());
            }

            return states;
        }
    }

    private void replaceOrRemove(String placeholder, List<String> dst, double chance) {
        if (chance > 0) {
            LoreUtils.replacePlaceholder(placeholder, dst, NumberUtil.format(chance));
        } else {
            LoreUtils.removePlaceholder(placeholder, dst, false);
        }
    }
}
