package cc.mewcraft.enchantment.gui.gui

import cc.mewcraft.enchantment.gui.EnchantGuiPlugin
import cc.mewcraft.enchantment.gui.api.UiEnchant
import cc.mewcraft.enchantment.gui.api.UiEnchantProvider
import cc.mewcraft.enchantment.gui.config.EnchantGuiSettings
import cc.mewcraft.enchantment.gui.util.miniMessage
import cc.mewcraft.enchantment.gui.util.translatable
import cc.mewcraft.enchantment.gui.util.wrapper
import com.google.inject.Inject
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import xyz.xenondevs.invui.animation.impl.RandomAnimation
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.SlotElement
import xyz.xenondevs.invui.gui.structure.Markers
import xyz.xenondevs.invui.inventory.Inventory
import xyz.xenondevs.invui.inventory.VirtualInventory
import xyz.xenondevs.invui.item.Item
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.SimpleItem
import xyz.xenondevs.invui.window.Window

class EnchantMenu
@Inject constructor(
    private val cache: ItemProviderCache,
    private val plugin: EnchantGuiPlugin,
    private val settings: EnchantGuiSettings,

    backItem: BackItem,
    forwardItem: ForwardItem,
) {
    private val compatibilityCheckInventory = VirtualInventory(1)
    private val enchantmentLookupInventory = VirtualInventory(1)

    private val makeItemFunction: (UiEnchant) -> Item = {
        PreviewItem.withStateChangeHandler(
            { player, _ -> player.playSound(settings.switchSound) },
            *cache[it]
        )
    }

    private val gui = PagedGui.items()
        .setStructure(*settings.guiLayout)
        .addIngredient(
            '#', SimpleItem(
                ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(
                    "".miniMessage().wrapper()
                )
            )
        )
        .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
        .addIngredient('i', compatibilityCheckInventory)
        .addIngredient('s', enchantmentLookupInventory)
        .addIngredient('<', backItem)
        .addIngredient('>', forwardItem)
        .setContent(UiEnchantProvider.all().map(makeItemFunction)) // initially, show all enchantments
        .build()

    private val window = Window.single()
        .setTitle("menu.enchantment.title".translatable().wrapper())
        .setGui(gui)

    fun `open`(viewer: Player) {
        setUpdateHandler(viewer)
        window.addOpenHandler {
            viewer.playSound(settings.openSound)
        }
        window.addCloseHandler {
            returnItems(compatibilityCheckInventory, viewer)
            returnItems(enchantmentLookupInventory, viewer)
        }
        window.open(viewer)
    }

    private fun setUpdateHandler(viewer: Player) {
        // Filter out all the enchantments that is applicable to the test item
        compatibilityCheckInventory.setPostUpdateHandler {
            filterEnchants(compatibilityCheckInventory) { test ->
                viewer.playSound(settings.testSound)
                plugin.languages.of("msg_filter_out_applicable")
                    .resolver(Placeholder.component("item", test.displayName()))
                    .send(viewer)
                UiEnchantProvider.filter { it.canEnchantment(test) }.map(makeItemFunction).toList()
            }
        }

        // Filter out all the enchantments that present in the test item
        enchantmentLookupInventory.setPostUpdateHandler {
            filterEnchants(enchantmentLookupInventory) { test ->
                viewer.playSound(settings.testSound)
                plugin.languages.of("msg_filter_out_current")
                    .resolver(Placeholder.component("item", test.displayName()))
                    .send(viewer)
                val itemMeta = test.itemMeta
                val enchantments: Set<Key> = if (itemMeta is EnchantmentStorageMeta)
                    itemMeta.storedEnchants.keys.map { it.key }.toSet() else
                    itemMeta.enchants.keys.map { it.key }.toSet()
                UiEnchantProvider.filter { it.key() in enchantments }.map(makeItemFunction)
            }
        }
    }

    private fun returnItems(inventory: Inventory, player: Player) {
        for (item in inventory.unsafeItems) {
            item?.let { player.inventory.addItem(it) } // must filter out null elements
        }
    }

    private fun filterEnchants(inventory: VirtualInventory, action: (ItemStack) -> List<Item>) {
        val test = inventory.getItem(0)
        val content: List<Item> = test?.let(action) ?: UiEnchantProvider.all().map(makeItemFunction)
        gui.setContent(content)

        // Must last to play animation
        test?.let {
            gui.playAnimation(
                RandomAnimation(1, false)
            ) { (it is SlotElement.ItemSlotElement) && (it.item is PreviewItem) }
        }
    }
}
