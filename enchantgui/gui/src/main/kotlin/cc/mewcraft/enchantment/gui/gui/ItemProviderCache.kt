package cc.mewcraft.enchantment.gui.gui

import cc.mewcraft.enchantment.gui.api.ChargeableUiEnchant
import cc.mewcraft.enchantment.gui.api.UiEnchant
import cc.mewcraft.enchantment.gui.config.EnchantGuiSettings
import cc.mewcraft.enchantment.gui.util.*
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.google.inject.Inject
import com.google.inject.Singleton
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.ItemWrapper
import xyz.xenondevs.invui.item.builder.ItemBuilder
import java.time.Duration

/**
 * This class provides loading cache that takes [UiEnchant] as key and returns an array of [ItemProvider].
 * The returned [ItemProvider] then can be used to construct the content of gui for display purposes.
 */
@Singleton
class ItemProviderCache
@Inject constructor(
    private val settings: EnchantGuiSettings,
    private val targetTranslator: TargetTranslator,
) {
    private val itemProviderCache: LoadingCache<UiEnchant, Array<ItemProvider>> = CacheBuilder.newBuilder()
        .expireAfterAccess(Duration.ofHours(2))
        .build(ItemProviderCacheLoader())

    operator fun get(key: UiEnchant): Array<ItemProvider> =
        itemProviderCache.getUnchecked(key)

    private inner class ItemProviderCacheLoader : CacheLoader<UiEnchant, Array<ItemProvider>>() {
        @Throws(Exception::class)
        override fun load(enchant: UiEnchant): Array<ItemProvider> {
            val min = enchant.minimumLevel()
            val max = enchant.maximumLevel()
            val states = ArrayList<ItemProvider>(max)

            for (level in (min..max)) {
                // Prepare all the values needed to create the enchantment icon
                val displayName = enchant.displayName()[level]?.let { settings.displayNameFormat.replace("<enchantment_display_name>", it) } ?: NULL
                val description = enchant.description()[level]?.toMutableList() ?: emptyList()
                val rarity = enchant.rarity().name
                val targets = enchant.enchantmentTargets()
                    .map { targetTranslator.translate(it) }
                    .reduce { t1, t2 -> "$t1, $t2" }
                val minLevel = enchant.minimumLevel().toString()
                val maxLevel = enchant.maximumLevel().toString()

                val loreFormat = settings.loreFormat.toMutableList() // This list will be modified multiple times below

                // Make the lore that is common to all enchantments
                Lores.replacePlaceholder("<enchantment_description>", loreFormat, description)
                Lores.replacePlaceholder("<enchantment_rarity>", loreFormat, rarity)
                Lores.replacePlaceholder("<enchantment_target_list>", loreFormat, targets)
                Lores.replacePlaceholder("<enchantment_level_min>", loreFormat, minLevel)
                Lores.replacePlaceholder("<enchantment_level_max>", loreFormat, maxLevel)

                // Make the lore that describes conflict
                if (enchant.conflict().isNotEmpty()) {
                    val conflict = enchant.conflict()
                        .chunked(3)
                        .map { chunk -> chunk.map { it.name() }.reduce { e1, e2 -> "$e1, $e2" } }
                    Lores.replacePlaceholder("<conflict>", loreFormat, settings.loreFormatConflict)
                    Lores.replacePlaceholder("<enchantment_conflict_list>", loreFormat, conflict)
                } else {
                    Lores.removePlaceholder("<conflict>", loreFormat, keep = false)
                }

                // Make the lore that describes charging
                if (enchant is ChargeableUiEnchant) {
                    Lores.replacePlaceholder("<charging>", loreFormat, settings.loreFormatCharging)
                    Lores.replacePlaceholder("<enchantment_charges_fuel_item>", loreFormat, enchant.fuel)
                    Lores.replacePlaceholder("<enchantment_charges_consume_amount>", loreFormat, enchant.fuelConsume[level]?.let { Numbers.format(it.toDouble()) } ?: NULL)
                    Lores.replacePlaceholder("<enchantment_charges_recharge_amount>", loreFormat, enchant.fuelRecharge[level]?.let { Numbers.format(it.toDouble()) } ?: NULL)
                    Lores.replacePlaceholder("<enchantment_charges_max_amount>", loreFormat, enchant.maximumFuel[level]?.let { Numbers.format(it.toDouble()) } ?: NULL)
                } else {
                    Lores.removePlaceholder("<charging>", loreFormat, keep = false)
                }

                // Make the lore that describes how to obtain it
                if (loreFormat.contains("<obtaining>")) {
                    Lores.replacePlaceholder("<obtaining>", loreFormat, settings.loreFormatObtaining)
                    replaceOrRemove("<enchantment_obtain_chance_enchanting>", loreFormat, enchant.enchantingChance())
                    replaceOrRemove("<enchantment_obtain_chance_villager>", loreFormat, enchant.villagerTradeChance())
                    replaceOrRemove("<enchantment_obtain_chance_loot_generation>", loreFormat, enchant.lootGenerationChance())
                    replaceOrRemove("<enchantment_obtain_chance_fishing>", loreFormat, enchant.fishingChance())
                    replaceOrRemove("<enchantment_obtain_chance_mob_spawning>", loreFormat, enchant.mobSpawningChance())
                }

                val builder = ItemBuilder(settings.itemMaterial).apply {
                    this.displayName = displayName.miniMessage().wrapper()
                    this.lore = loreFormat.miniMessage().wrapper()
                }

                states += ItemWrapper(builder.get())
            }

            return states.toTypedArray()
        }
    }
}

private const val NULL: String = "NULL"

private fun replaceOrRemove(placeholder: String, dst: MutableList<String>, chance: Double) =
    if (chance > 0) {
        Lores.replacePlaceholder(placeholder, dst, Numbers.format(chance))
    } else {
        Lores.removePlaceholder(placeholder, dst, keep = false)
    }
