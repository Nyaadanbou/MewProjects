package cc.mewcraft.enchantment.gui.api

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import net.kyori.adventure.key.Keyed
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

/**
 * Represents an enchantment that can be displayed in the GUI.
 *
 * The methods are necessary to render all the enchantment information
 * (such as 'displayName', 'description' and 'conflicts')
 * for the items in the GUI.
 */
interface UiEnchant : Keyed /* Keyed is used to identify enchantments */ {
    // --- Display
    /**
     * @return name of this enchantment, without levels
     */
    fun name(): String

    // Notes: displayName and description vary depending on the enchantment level.
    // Hence, these two methods return a map where key is the enchantment level.
    fun displayName(): Map<Int, String>
    fun description(): Map<Int, List<String>>

    /**
     * Creates mappings to transform certain level to corresponding value.
     *
     * @param func a mapper transforming certain level to corresponding value
     * @param <T>  the value type
     * @return a map containing all values of each level
    </T> */
    fun <T> levelScale(func: (Int) -> T): Map<Int, T> {
        return Int2ObjectArrayMap<T>().apply {
            (minimumLevel()..maximumLevel()).forEach { set(it, func.invoke(it)) }
        }
    }

    // --- Target
    fun canEnchantment(item: ItemStack): Boolean
    fun enchantmentTargets(): List<UiEnchantTarget>

    // --- Rarity
    fun rarity(): UiEnchantRarity

    // --- Obtaining
    fun enchantingChance(): Double
    fun villagerTradeChance(): Double
    fun lootGenerationChance(): Double
    fun fishingChance(): Double
    fun mobSpawningChance(): Double

    // --- Conflicts
    /**
     * Returns a list of non-null enchants that conflict with this enchant.
     *
     * The enchants that exists in the configuration of backed enchant plugin
     * **but not** exists in [UiEnchantProvider] will be excluded
     * from the returned list.
     *
     * @return a list of non-null enchants that conflict with this enchant
     */
    fun conflict(): List<UiEnchant>
    fun conflictsWith(other: Enchantment): Boolean

    // --- Min/Max enchantment levels
    fun minimumLevel(): Int
    fun maximumLevel(): Int
}
