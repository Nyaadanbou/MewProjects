package cc.mewcraft.enchantment.gui.gui

import cc.mewcraft.enchantment.gui.config.EnchantGuiSettings
import cc.mewcraft.enchantment.gui.util.translatable
import cc.mewcraft.enchantment.gui.util.wrapper
import com.google.inject.Inject
import org.bukkit.Material
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.ItemBuilder

class BackItem
@Inject constructor(
    settings: EnchantGuiSettings,
) : AnimatedPageItem(settings, false) {
    override fun getItemProvider(gui: PagedGui<*>): ItemProvider {
        return ItemBuilder(Material.SPECTRAL_ARROW).apply {
            displayName = "menu.enchantment.previous_page".translatable().wrapper()
        }
    }
}
