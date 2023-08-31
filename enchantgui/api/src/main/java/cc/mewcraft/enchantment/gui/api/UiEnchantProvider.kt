package cc.mewcraft.enchantment.gui.api;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.ClassPath;
import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class provides static access to the registered {@link UiEnchant UiEnchants}.
 */
public final class UiEnchantProvider {
    private static final Map<Key, UiEnchant> elements = new ConcurrentHashMap<>();
    private static final Set<UiEnchantAdapter<?, ?>> adapters = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * Initializes this provider.
     */
    @SuppressWarnings("unchecked")
    public static void initialize(UiEnchantPlugin plugin)
        throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // Load all adapter classes at runtime
        Set<Class<UiEnchantAdapter<?, ?>>> adapterClazz = ClassPath.from(plugin.getClazzLoader())
            .getTopLevelClasses("cc.mewcraft.enchantment.gui.adapter")
            .stream()
            .map(ClassPath.ClassInfo::load)
            .filter(UiEnchantAdapter.class::isAssignableFrom)
            .map(clazz -> (Class<UiEnchantAdapter<?, ?>>) clazz)
            .collect(Collectors.toSet());

        // Add all adapter instances to the set
        for (final Class<UiEnchantAdapter<?, ?>> clazz : adapterClazz) {
            adapters.add(plugin.getInjector().getInstance(clazz));
        }

        // Initialize all adapters
        for (final UiEnchantAdapter<?, ?> adapter : UiEnchantProvider.adapters) {
            adapter.initialize();
        }
    }

    /**
     * @return a modifiable map of {@link UiEnchant EnchantmentElements}
     */
    public static @NotNull Map<Key, UiEnchant> asMap() {
        return ImmutableMap.copyOf(UiEnchantProvider.elements);
    }

    // --- Contains

    public static boolean containsKey(@NotNull Key key) {
        Preconditions.checkNotNull(key);
        return UiEnchantProvider.elements.containsKey(key);
    }

    public static boolean containsKey(@Subst("blast_mining") @NotNull String key) {
        return containsKey(Key.key(Key.MINECRAFT_NAMESPACE, key));
    }

    // --- Get

    public static @Nullable UiEnchant get(@NotNull Key key) {
        Preconditions.checkNotNull(key);
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

    public static @NotNull UiEnchant getOrThrow(@NotNull String key) {
        UiEnchant enchantment = get(key);

        if (enchantment == null) {
            throw new NullPointerException(key);
        }

        return enchantment;
    }

    // --- Filter

    public static @NotNull Stream<UiEnchant> all() {
        return UiEnchantProvider.elements.values().stream();
    }

    public static @NotNull Stream<UiEnchant> filter(@NotNull Predicate<UiEnchant> test) {
        Preconditions.checkNotNull(test, "test");
        return UiEnchantProvider.elements.values().stream().filter(test);
    }

    // --- Register/unregister

    public static void register(@NotNull UiEnchant element) {
        Preconditions.checkNotNull(element, "element");
        UiEnchantProvider.elements.put(element.key(), element);
    }

    public static void unregister(@NotNull Key key) {
        Preconditions.checkNotNull(key, "id");
        UiEnchantProvider.elements.remove(key);
    }

    private UiEnchantProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}
