package cc.mewcraft.enchantment.gui.api

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.inventory.ItemStack

/**
 * A UiEnchant that is chargeable.
 */
class ChargeableUiEnchant(
    baseEnchant: UiEnchant,
    fuelItem: ItemStack, // it's the name of fuel item in MiniMessage string representation
    fuelConsumeMapping: (Int) -> Int,
    fuelRechargeMapping: (Int) -> Int,
    maximumFuelMapping: (Int) -> Int,
) : UiEnchant by baseEnchant {
    val fuel: String = fuelItem.let {
        val itemMeta = fuelItem.itemMeta
        val component = if (itemMeta.hasDisplayName()) itemMeta.displayName() else Component.translatable(fuelItem)
        val miniMessage = MiniMessage.builder().strict(true).build() // make it strict to generate closed tags
        component?.let(miniMessage::serialize) ?: ""
    }
    val fuelConsume: Map<Int, Int> = levelScale(fuelConsumeMapping)
    val fuelRecharge: Map<Int, Int> = levelScale(fuelRechargeMapping)
    val maximumFuel: Map<Int, Int> = levelScale(maximumFuelMapping)
}
