package cc.mewcraft.enchantment.gui.adapter

import cc.mewcraft.enchantment.gui.api.UiEnchant
import cc.mewcraft.enchantment.gui.api.UiEnchantAdapter
import cc.mewcraft.enchantment.gui.api.UiEnchantPlugin
import cc.mewcraft.enchantment.gui.api.UiEnchantTarget
import com.google.inject.Inject
import com.google.inject.Singleton
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget

// TODO implement vanilla enchantments into the menu
@Singleton
class MinecraftEnchantAdapter
@Inject constructor(
    private val plugin: UiEnchantPlugin,
) : UiEnchantAdapter<Enchantment, EnchantmentTarget> {
    override fun initialize() {}
    override fun canInitialize(): Boolean {
        return true // vanilla enchantments are always available
    }

    override fun adaptEnchantment(rawEnchant: Enchantment): UiEnchant {
        TODO()
    }

    override fun adaptEnchantmentTarget(rawTarget: EnchantmentTarget): UiEnchantTarget {
        TODO()
    }
}
