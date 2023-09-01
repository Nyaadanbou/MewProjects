package cc.mewcraft.enchantment.gui.gui

import org.bukkit.entity.Player
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.impl.CycleItem
import java.util.function.BiConsumer

/**
 * This class is just a representation of enchantment preview item.
 */
internal open class PreviewItem(
    startState: Int,
    states: Array<out ItemProvider>,
) : CycleItem(startState, *states) {
    companion object {
        // The two static methods are copied from CycleItem with the difference
        // in that the return type is changed to PreviewItem instead of CycleItem
        @JvmStatic
        fun withStateChangeHandler(
            stateChangeHandler: BiConsumer<Player, Int>,
            vararg states: ItemProvider
        ): PreviewItem {
            return withStateChangeHandler(stateChangeHandler, 0, *states)
        }

        @JvmStatic
        fun withStateChangeHandler(
            stateChangeHandler: BiConsumer<Player, Int>,
            startState: Int,
            vararg states: ItemProvider
        ): PreviewItem {
            return object : PreviewItem(startState, states) {
                override fun handleStateChange(player: Player?, state: Int) {
                    stateChangeHandler.accept(player!!, state)
                }
            }
        }
    }
}
