package cc.mewcraft.enchantment.gui.api;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Represents an enchantment that can be displayed in the GUI.
 * <p>
 * The methods are necessary to render all the enchantment information
 * (such as 'displayName', 'description' and 'conflicts')
 * for the items in the GUI.
 */
public interface UiEnchant extends Keyed /* Keyed is used to identify enchantments */ {
    // --- Display

    /**
     * @return name of this enchantment, without levels
     */
    @NotNull String name();

    // Notes: displayName and description vary depending on the enchantment level.
    // Hence, these two methods return a map where key is the enchantment level.

    @NotNull Map<Integer, String> displayName();

    @NotNull Map<Integer, List<String>> description();

    /**
     * Creates mappings to transform certain level to corresponding value.
     *
     * @param func a mapper transforming certain level to corresponding value
     * @param <T>  the value type
     * @return a map containing all values of each level
     */
    default <T> @NotNull Map<Integer, T> scaleMapper(@NotNull Function<Integer, T> func) {
        Int2ObjectMap<T> int2ObjectArrayMap = new Int2ObjectArrayMap<>();
        IntStream.range(minimumLevel(), maximumLevel() + 1).forEachOrdered(t -> int2ObjectArrayMap.put(t, func.apply(t)));
        return int2ObjectArrayMap;
    }

    // --- Target

    boolean canEnchantment(@NotNull ItemStack item);

    @NotNull List<UiEnchantTarget> enchantmentTargets();

    // --- Rarity

    @NotNull UiEnchantRarity rarity();

    // --- Obtaining

    double enchantingChance();

    double villagerTradeChance();

    double lootGenerationChance();

    double fishingChance();

    double mobSpawningChance();

    // --- Conflicts

    @NotNull List<Key> conflict();

    boolean conflictsWith(@NotNull Enchantment other);

    // --- Min/Max enchantment levels

    int minimumLevel();

    int maximumLevel();
}
