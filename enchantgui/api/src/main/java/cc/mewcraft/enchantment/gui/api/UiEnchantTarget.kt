package cc.mewcraft.enchantment.gui.api

import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.inventory.ItemStack

/**
 * Represents item stacks to which the enchantment can be applied.
 */
enum class UiEnchantTarget {
    // --- Biggest categories
    ALL {
        override fun includes(item: Material): Boolean {
            return true
        }
    },
    WEAPON {
        override fun includes(item: Material): Boolean {
            return Tag.ITEMS_SWORDS.isTagged(item) || Tag.ITEMS_AXES.isTagged(item)
        }
    },
    TOOL {
        override fun includes(item: Material): Boolean {
            return Tag.ITEMS_TOOLS.isTagged(item)
        }
    },
    ARMOR {
        override fun includes(item: Material): Boolean {
            return HELMET.includes(item) || CHESTPLATE.includes(item) || LEGGINGS.includes(item) || BOOTS.includes(item)
        }
    },

    // --- Armor specifics
    HELMET {
        override fun includes(item: Material): Boolean {
            return item == Material.LEATHER_HELMET || item == Material.CHAINMAIL_HELMET || item == Material.DIAMOND_HELMET || item == Material.IRON_HELMET || item == Material.GOLDEN_HELMET || item == Material.TURTLE_HELMET || item == Material.NETHERITE_HELMET
        }
    },
    CHESTPLATE {
        override fun includes(item: Material): Boolean {
            return item == Material.LEATHER_CHESTPLATE || item == Material.CHAINMAIL_CHESTPLATE || item == Material.IRON_CHESTPLATE || item == Material.DIAMOND_CHESTPLATE || item == Material.GOLDEN_CHESTPLATE || item == Material.NETHERITE_CHESTPLATE
        }
    },
    LEGGINGS {
        override fun includes(item: Material): Boolean {
            return item == Material.LEATHER_LEGGINGS || item == Material.CHAINMAIL_LEGGINGS || item == Material.IRON_LEGGINGS || item == Material.DIAMOND_LEGGINGS || item == Material.GOLDEN_LEGGINGS || item == Material.NETHERITE_LEGGINGS
        }
    },
    BOOTS {
        override fun includes(item: Material): Boolean {
            return item == Material.LEATHER_BOOTS || item == Material.CHAINMAIL_BOOTS || item == Material.IRON_BOOTS || item == Material.DIAMOND_BOOTS || item == Material.GOLDEN_BOOTS || item == Material.NETHERITE_BOOTS
        }
    },
    ELYTRA {
        override fun includes(item: Material): Boolean {
            return item == Material.ELYTRA
        }
    },

    // --- Weapon specifics
    SWORD {
        override fun includes(item: Material): Boolean {
            return Tag.ITEMS_SWORDS.isTagged(item) || Tag.ITEMS_AXES.isTagged(item) || TRIDENT.includes(item)
        }
    },
    TRIDENT {
        override fun includes(item: Material): Boolean {
            return item == Material.TRIDENT
        }
    },
    AXE {
        override fun includes(item: Material): Boolean {
            return Tag.ITEMS_AXES.isTagged(item)
        }
    },
    BOW {
        override fun includes(item: Material): Boolean {
            return item == Material.BOW
        }
    },
    CROSSBOW {
        override fun includes(item: Material): Boolean {
            return item == Material.CROSSBOW
        }
    },

    // --- Tool specifics
    HOE {
        override fun includes(item: Material): Boolean {
            return Tag.ITEMS_HOES.isTagged(item)
        }
    },
    PICKAXE {
        override fun includes(item: Material): Boolean {
            return Tag.ITEMS_PICKAXES.isTagged(item)
        }
    },
    SHOVEL {
        override fun includes(item: Material): Boolean {
            return Tag.ITEMS_SHOVELS.isTagged(item)
        }
    },
    FISHING_ROD {
        override fun includes(item: Material): Boolean {
            return item == Material.FISHING_ROD
        }
    };

    /**
     * Check whether this target includes the specified item.
     *
     * @param item The item to check
     * @return True if the target includes the item
     */
    abstract fun includes(item: Material): Boolean

    /**
     * Check whether this target includes the specified item.
     *
     * @param item The item to check
     * @return True if the target includes the item
     */
    fun includes(item: ItemStack): Boolean {
        return includes(item.type)
    }
}
