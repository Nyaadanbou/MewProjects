package cc.mewcraft.enchantment.gui.adapter

import cc.mewcraft.enchantment.gui.api.*
import com.google.inject.Inject
import com.google.inject.Singleton
import net.kyori.adventure.key.Key
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import su.nightexpress.excellentenchants.enchantment.EnchantRegistry
import su.nightexpress.excellentenchants.enchantment.impl.ExcellentEnchant
import su.nightexpress.excellentenchants.enchantment.type.FitItemType
import su.nightexpress.excellentenchants.enchantment.type.ObtainType

@Singleton
class ExcellentEnchantAdapter
@Inject constructor(
    private val plugin: UiEnchantPlugin,
) : UiEnchantAdapter<ExcellentEnchant, FitItemType> {
    override fun initialize() {
        if (!canInitialize()) {
            return
        }
        for (enchant in EnchantRegistry.getRegistered()) {
            UiEnchantProvider.register(adaptEnchantment(enchant))
        }
    }

    override fun canInitialize(): Boolean {
        return plugin.isPluginPresent("ExcellentEnchants")
    }

    override fun adaptEnchantment(rawEnchant: ExcellentEnchant): UiEnchant {
        val uiEnchant: UiEnchant = object : UiEnchant {
            override fun name(): String {
                return rawEnchant.displayName
            }

            override fun displayName(): Map<Int, String> {
                return levelScale { rawEnchant.getNameFormatted(it) }
            }

            override fun description(): Map<Int, List<String>> {
                return levelScale { rawEnchant.getDescription(it) }
            }

            override fun canEnchantment(item: ItemStack): Boolean {
                return rawEnchant.canEnchantItem(item)
            }

            override fun enchantmentTargets(): List<UiEnchantTarget> {
                return rawEnchant.fitItemTypes.map { adaptEnchantmentTarget(it) }
            }

            override fun rarity(): UiEnchantRarity {
                return UiEnchantRarity(rawEnchant.tier.name, rawEnchant.tier.color)
            }

            override fun enchantingChance(): Double {
                return rawEnchant.getObtainChance(ObtainType.ENCHANTING)
            }

            override fun villagerTradeChance(): Double {
                return rawEnchant.getObtainChance(ObtainType.VILLAGER)
            }

            override fun lootGenerationChance(): Double {
                return rawEnchant.getObtainChance(ObtainType.LOOT_GENERATION)
            }

            override fun fishingChance(): Double {
                return rawEnchant.getObtainChance(ObtainType.FISHING)
            }

            override fun mobSpawningChance(): Double {
                return rawEnchant.getObtainChance(ObtainType.MOB_SPAWNING)
            }

            override fun conflict(): List<UiEnchant> {
                return rawEnchant.conflicts.mapNotNull { UiEnchantProvider[it] }
            }

            override fun conflictsWith(other: Enchantment): Boolean {
                return rawEnchant.conflictsWith(other)
            }

            override fun minimumLevel(): Int {
                return rawEnchant.startLevel
            }

            override fun maximumLevel(): Int {
                return rawEnchant.maxLevel
            }

            override fun key(): Key {
                return rawEnchant.key()
            }
        }

        return when {
            rawEnchant.isChargesEnabled -> ChargeableUiEnchant(
                uiEnchant,
                rawEnchant.chargesFuel.item,
                rawEnchant::getChargesConsumeAmount,
                rawEnchant::getChargesRechargeAmount,
                rawEnchant::getChargesMax,
            )

            else -> uiEnchant
        }
    }

    override fun adaptEnchantmentTarget(rawTarget: FitItemType): UiEnchantTarget {
        return when (rawTarget) {
            FitItemType.UNIVERSAL -> UiEnchantTarget.ALL
            FitItemType.HELMET -> UiEnchantTarget.HELMET
            FitItemType.CHESTPLATE -> UiEnchantTarget.CHESTPLATE
            FitItemType.LEGGINGS -> UiEnchantTarget.LEGGINGS
            FitItemType.BOOTS -> UiEnchantTarget.BOOTS
            FitItemType.ELYTRA -> UiEnchantTarget.ELYTRA
            FitItemType.WEAPON -> UiEnchantTarget.WEAPON
            FitItemType.TOOL -> UiEnchantTarget.TOOL
            FitItemType.ARMOR -> UiEnchantTarget.ARMOR
            FitItemType.SWORD -> UiEnchantTarget.SWORD
            FitItemType.TRIDENT -> UiEnchantTarget.TRIDENT
            FitItemType.AXE -> UiEnchantTarget.AXE
            FitItemType.BOW -> UiEnchantTarget.BOW
            FitItemType.CROSSBOW -> UiEnchantTarget.CROSSBOW
            FitItemType.HOE -> UiEnchantTarget.HOE
            FitItemType.PICKAXE -> UiEnchantTarget.PICKAXE
            FitItemType.SHOVEL -> UiEnchantTarget.SHOVEL
            FitItemType.FISHING_ROD -> UiEnchantTarget.FISHING_ROD
        }
    }
}
