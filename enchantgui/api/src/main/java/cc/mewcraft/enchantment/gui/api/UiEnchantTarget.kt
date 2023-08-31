package cc.mewcraft.enchantment.gui.api;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Represents item stacks to which the enchantment can be applied.
 */
public enum UiEnchantTarget {
    // --- Biggest categories

    ALL {
        @Override public boolean includes(final @NotNull Material item) {
            return true;
        }
    },

    WEAPON {
        @Override public boolean includes(final @NotNull Material item) {
            return Tag.ITEMS_SWORDS.isTagged(item) || Tag.ITEMS_AXES.isTagged(item);
        }
    },

    TOOL {
        @Override public boolean includes(final @NotNull Material item) {
            return Tag.ITEMS_TOOLS.isTagged(item);
        }
    },

    ARMOR {
        @Override public boolean includes(final @NotNull Material item) {
            return HELMET.includes(item)
                   || CHESTPLATE.includes(item)
                   || LEGGINGS.includes(item)
                   || BOOTS.includes(item);
        }
    },

    // --- Armor specifics

    HELMET {
        @Override public boolean includes(final @NotNull Material item) {
            return item.equals(Material.LEATHER_HELMET)
                   || item.equals(Material.CHAINMAIL_HELMET)
                   || item.equals(Material.DIAMOND_HELMET)
                   || item.equals(Material.IRON_HELMET)
                   || item.equals(Material.GOLDEN_HELMET)
                   || item.equals(Material.TURTLE_HELMET)
                   || item.equals(Material.NETHERITE_HELMET);
        }
    },

    CHESTPLATE {
        @Override public boolean includes(final @NotNull Material item) {
            return item.equals(Material.LEATHER_CHESTPLATE)
                   || item.equals(Material.CHAINMAIL_CHESTPLATE)
                   || item.equals(Material.IRON_CHESTPLATE)
                   || item.equals(Material.DIAMOND_CHESTPLATE)
                   || item.equals(Material.GOLDEN_CHESTPLATE)
                   || item.equals(Material.NETHERITE_CHESTPLATE);
        }
    },

    LEGGINGS {
        @Override public boolean includes(final @NotNull Material item) {
            return item.equals(Material.LEATHER_LEGGINGS)
                   || item.equals(Material.CHAINMAIL_LEGGINGS)
                   || item.equals(Material.IRON_LEGGINGS)
                   || item.equals(Material.DIAMOND_LEGGINGS)
                   || item.equals(Material.GOLDEN_LEGGINGS)
                   || item.equals(Material.NETHERITE_LEGGINGS);
        }
    },

    BOOTS {
        @Override public boolean includes(final @NotNull Material item) {
            return item.equals(Material.LEATHER_BOOTS)
                   || item.equals(Material.CHAINMAIL_BOOTS)
                   || item.equals(Material.IRON_BOOTS)
                   || item.equals(Material.DIAMOND_BOOTS)
                   || item.equals(Material.GOLDEN_BOOTS)
                   || item.equals(Material.NETHERITE_BOOTS);
        }
    },

    ELYTRA {
        @Override public boolean includes(final @NotNull Material item) {
            return item == Material.ELYTRA;
        }
    },

    // --- Weapon specifics

    SWORD {
        @Override public boolean includes(final @NotNull Material item) {
            return Tag.ITEMS_SWORDS.isTagged(item)
                   || Tag.ITEMS_AXES.isTagged(item)
                   || TRIDENT.includes(item);
        }
    },

    TRIDENT {
        @Override public boolean includes(final @NotNull Material item) {
            return item == Material.TRIDENT;
        }
    },

    AXE {
        @Override public boolean includes(final @NotNull Material item) {
            return Tag.ITEMS_AXES.isTagged(item);
        }
    },

    BOW {
        @Override public boolean includes(final @NotNull Material item) {
            return item == Material.BOW;
        }
    },

    CROSSBOW {
        @Override public boolean includes(final @NotNull Material item) {
            return item == Material.CROSSBOW;
        }
    },

    // --- Tool specifics

    HOE {
        @Override public boolean includes(final @NotNull Material item) {
            return Tag.ITEMS_HOES.isTagged(item);
        }
    },

    PICKAXE {
        @Override public boolean includes(final @NotNull Material item) {
            return Tag.ITEMS_PICKAXES.isTagged(item);
        }
    },

    SHOVEL {
        @Override public boolean includes(final @NotNull Material item) {
            return Tag.ITEMS_SHOVELS.isTagged(item);
        }
    },

    FISHING_ROD {
        @Override public boolean includes(final @NotNull Material item) {
            return item == Material.FISHING_ROD;
        }
    };

    /**
     * Check whether this target includes the specified item.
     *
     * @param item The item to check
     * @return True if the target includes the item
     */
    public abstract boolean includes(@NotNull Material item);

    /**
     * Check whether this target includes the specified item.
     *
     * @param item The item to check
     * @return True if the target includes the item
     */
    public boolean includes(@NotNull ItemStack item) {
        return includes(item.getType());
    }
}
