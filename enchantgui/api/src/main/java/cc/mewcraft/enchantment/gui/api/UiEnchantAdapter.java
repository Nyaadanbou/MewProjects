package cc.mewcraft.enchantment.gui.api;

/**
 * Represents an adapter than converts arbitrary enchantment type to {@link UiEnchant}.
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

    UiEnchant adaptEnchantment(E enchantment);

    UiEnchantTarget adaptEnchantmentTarget(T enchantmentTarget);
}
