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
import xyz.xenondevs.invui.inventory.get
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
    private val compatibilityCheckInventory = VirtualInventory(1) // 1 slot is enough.
    private val enchantmentLookupInventory = VirtualInventory(1) // The same.

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
        .setContent(UiEnchantProvider.all().map { it.toItem() }) // Initially, show all enchantments.
        .build()

    private val window = Window.single()
        .setTitle("menu.enchantment.title".translatable().wrapper())
        .setGui(gui)

    fun showMenu(viewer: Player) {
        // Filter out all the enchantments that is applicable to the test item.
        compatibilityCheckInventory.setPostUpdateHandler {
            compatibilityCheckInventory[0].generateGuiContent { test: ItemStack ->
                viewer.playSound(settings.testSound)
                plugin.languages.of("msg_filter_out_applicable")
                    .resolver(Placeholder.component("item", test.displayName()))
                    .send(viewer)
                UiEnchantProvider.filter {
                    it.canEnchantment(test)
                }.map {
                    it.toItem()
                }
            }
        }

        // Filter out all the enchantments that present in the test item.
        enchantmentLookupInventory.setPostUpdateHandler {
            enchantmentLookupInventory[0].generateGuiContent { test: ItemStack ->
                viewer.playSound(settings.testSound)
                plugin.languages.of("msg_filter_out_current")
                    .resolver(Placeholder.component("item", test.displayName()))
                    .send(viewer)
                val itemMeta = test.itemMeta
                val enchantments: Set<Key> = if (itemMeta is EnchantmentStorageMeta)
                    itemMeta.storedEnchants.keys.map { it.key }.toSet() else
                    itemMeta.enchants.keys.map { it.key }.toSet()
                UiEnchantProvider.filter {
                    it.key() in enchantments
                }.map {
                    it.toItem()
                }
            }
        }

        // Add open sound.
        window.addOpenHandler {
            viewer.playSound(settings.openSound)
        }

        // Return items when gui is closed.
        window.addCloseHandler {
            compatibilityCheckInventory.returnItems(viewer)
            enchantmentLookupInventory.returnItems(viewer)
        }

        window.open(viewer)
    }

    /**
     * Constructs an [Item] from the UiEnchant.
     */
    private fun UiEnchant.toItem() =
        PreviewItem.withStateChangeHandler(cache[this]) { player, _ ->
            player.playSound(settings.switchSound)
        }


    /**
     * Returns items stored in the inventory to the player viewer.
     */
    private fun Inventory.returnItems(player: Player) {
        for (item in this.unsafeItems) {
            // Must filter out null elements.
            item?.let { player.inventory.addItem(it) }
        }
    }

    /**
     * Set the content of the paged gui depending on the receiver item,
     * where the gui content is aka the current enchantment item list.
     */
    private fun ItemStack?.generateGuiContent(generator: (ItemStack) -> List<Item>) {
        val content: List<Item> = if (this != null) {
            // If item is not full, generate filtered content using given function.
            generator(this)
        } else {
            // If it is null, generate complete content.
            UiEnchantProvider.all().map { it.toItem() }
        }

        gui.setContent(content)

        // Must play animation AFTER setting the content
        this?.let {
            gui.playAnimation(RandomAnimation(1, false)) {
                (it is SlotElement.ItemSlotElement) && (it.item is PreviewItem)
            }
        }
    }
}
