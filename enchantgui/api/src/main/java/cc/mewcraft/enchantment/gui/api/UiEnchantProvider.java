package cc.mewcraft.enchantment.gui.api;

import com.google.common.base.Preconditions;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * This class provides static access to the registered {@link UiEnchant UiEnchantments}.
 */
public class UiEnchantProvider {
    private static Map<Key, UiEnchant> elements = null;

    /**
     * @return a modifiable map of {@link UiEnchant EnchantmentElements}
     */
    public static @NotNull Map<Key, UiEnchant> asMap() {
        UiEnchantProvider.checkProviderInstance();
        return UiEnchantProvider.elements;
    }

    public static @NotNull Stream<UiEnchant> all() {
        UiEnchantProvider.checkProviderInstance();
        return UiEnchantProvider.elements.values().stream();
    }

    public static @NotNull Stream<UiEnchant> filter(@NotNull ItemStack itemStack) {
        Preconditions.checkNotNull(itemStack, "itemStack");
        UiEnchantProvider.checkProviderInstance();
        return UiEnchantProvider.elements.values().stream().filter(enchantment -> enchantment.canEnchantment(itemStack));
    }

    public static @NotNull Stream<UiEnchant> filter(@NotNull Predicate<UiEnchant> test) {
        Preconditions.checkNotNull(test, "test");
        UiEnchantProvider.checkProviderInstance();
        return UiEnchantProvider.elements.values().stream().filter(test);
    }

    public static boolean containsKey(@NotNull Key key) {
        Preconditions.checkNotNull(key);
        UiEnchantProvider.checkProviderInstance();
        return UiEnchantProvider.elements.containsKey(key);
    }

    public static boolean containsKey(@Subst("blast_mining") @NotNull String key) {
        return containsKey(Key.key(Key.MINECRAFT_NAMESPACE, key));
    }

    public static @Nullable UiEnchant get(@NotNull Key key) {
        Preconditions.checkNotNull(key);
        UiEnchantProvider.checkProviderInstance();
        return UiEnchantProvider.elements.get(key);
    }

    public static @Nullable UiEnchant get(@Subst("blast_mining") @NotNull String key) {
        return get(Key.key(Key.MINECRAFT_NAMESPACE, key));
    }

    public static @NotNull UiEnchant getOrThrow(@NotNull Key key) {
        UiEnchant enchantment = get(key);
        if (enchantment == null) {
            throw new NullPointerException(key.asString());
        }
        return enchantment;
    }

    public static @NotNull UiEnchant getOrThrow(@Subst("blast_mining") @NotNull String key) {
        UiEnchant enchantment = get(key);
        if (enchantment == null) {
            throw new NullPointerException(key);
        }
        return enchantment;
    }

    public static void register(@NotNull UiEnchant element) {
        Preconditions.checkNotNull(element, "element");

        if (UiEnchantProvider.elements == null) {
            UiEnchantProvider.elements = new ConcurrentHashMap<>();
        }
        UiEnchantProvider.elements.put(element.key(), element);
    }

    public static void unregister(@NotNull Key key) {
        Preconditions.checkNotNull(key, "id");

        if (UiEnchantProvider.elements == null) {
            return;
        }
        UiEnchantProvider.elements.remove(key);
    }

    public static void unregister() {
        UiEnchantProvider.elements.clear();
        UiEnchantProvider.elements = null;
    }

    private static void checkProviderInstance() {
        if (UiEnchantProvider.elements == null) {
            throw new IllegalStateException("Instance is not loaded yet");
        }
    }

    private UiEnchantProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}
