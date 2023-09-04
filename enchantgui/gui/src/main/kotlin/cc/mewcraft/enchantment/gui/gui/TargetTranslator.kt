package cc.mewcraft.enchantment.gui.gui

import cc.mewcraft.enchantment.gui.EnchantGuiPlugin
import cc.mewcraft.enchantment.gui.api.UiEnchantTarget
import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton
class TargetTranslator
@Inject constructor(
    private val plugin: EnchantGuiPlugin,
) {
    fun translate(target: UiEnchantTarget): String =
        plugin.languages.of("item_target_${target.name.lowercase()}").plain()
}
