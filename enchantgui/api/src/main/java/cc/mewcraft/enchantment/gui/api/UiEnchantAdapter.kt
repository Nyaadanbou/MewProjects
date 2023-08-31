package cc.mewcraft.enchantment.gui.api;

import org.jetbrains.annotations.NotNull;

/**
 * Represents an adapter than converts arbitrary enchantment type to {@link UiEnchant}.
 * <p>
 * All implementations should be put in the package: cc.mewcraft.enchantment.gui.adapter.
 * <p>
 * The following instances will be injected upon instance construction of this class:
 * <ul>
 *     <li>{@link UiEnchantPlugin}</li>
 * </ul>
 *
 * @param <E> enchantment type
 * @param <T> enchantment target type
 */
public interface UiEnchantAdapter<E, T> {
    /**
     * Initialize this adapter.
     * <p>
     * The implementation should register all available enchantments
     * via {@link UiEnchantProvider#register(UiEnchant)}.
     */
    void initialize();

    /**
     * Checks whether this adapter can be initialized or not.
     *
     * @return true if this adapter can be initialized; false otherwise
     */
    boolean canInitialize();

    @NotNull UiEnchant adaptEnchantment(@NotNull E enchantment);

    @NotNull UiEnchantTarget adaptEnchantmentTarget(@NotNull T enchantmentTarget);
}
