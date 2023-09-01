package cc.mewcraft.enchantment.gui.gui

import cc.mewcraft.enchantment.gui.config.EnchantGuiSettings
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import xyz.xenondevs.invui.animation.impl.ColumnAnimation
import xyz.xenondevs.invui.gui.SlotElement
import xyz.xenondevs.invui.item.impl.controlitem.PageItem

abstract class AnimatedPageItem(
    private val settings: EnchantGuiSettings,
    private val forward: Boolean,
) : PageItem(forward) {
    override fun handleClick(clickType: ClickType, player: Player, event: InventoryClickEvent) {
        if ((forward && gui.hasNextPage()) || (!forward && gui.hasPreviousPage())) {
            super.handleClick(clickType, player, event)
            player.playSound(settings.pageTurnSound)
            gui.playAnimation(ColumnAnimation(1, false)) {
                it is SlotElement.ItemSlotElement && it.item is PreviewItem
            }
        }
    }
}
