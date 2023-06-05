package cc.mewcraft.mewcore.util;

import cc.mewcraft.mewcore.cooldown.StackableCooldown;
import cc.mewcraft.mewcore.persistent.*;
import me.lucko.helper.cooldown.Cooldown;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public class PDCUtils {

    public static final PersistentDataType<byte[], UUID> UUID = new UUIDDataType();
    public static final PersistentDataType<byte[], double[]> DOUBLE_ARRAY = new DoubleArrayType();
    public static final PersistentDataType<byte[], String[]> STRING_ARRAY = new StringArrayType(StandardCharsets.UTF_8);
    public static final PersistentDataType<byte[], Cooldown> COOLDOWN = new CooldownType();
    public static final PersistentDataType<byte[], StackableCooldown> STACKABLE_COOLDOWN = new StackableCooldownType();

    public static @NotNull <Z> Optional<Z> get(@NotNull ItemStack holder, @NotNull PersistentDataType<?, Z> type, @NotNull NamespacedKey key) {
        ItemMeta meta = holder.getItemMeta();
        if (meta == null) return Optional.empty();

        return get(meta, type, key);
    }

    public static @NotNull <Z> Optional<Z> get(@NotNull PersistentDataHolder holder, @NotNull PersistentDataType<?, Z> type, @NotNull NamespacedKey key) {
        PersistentDataContainer container = holder.getPersistentDataContainer();
        if (container.has(key, type)) {
            return Optional.ofNullable(container.get(key, type));
        }
        return Optional.empty();
    }

    public static void set(@NotNull ItemStack holder, @NotNull NamespacedKey key, boolean value) {
        set(holder, PersistentDataType.INTEGER, key, value ? 1 : 0);
    }

    public static void set(@NotNull PersistentDataHolder holder, @NotNull NamespacedKey key, boolean value) {
        set(holder, PersistentDataType.INTEGER, key, value ? 1 : 0);
    }

    public static void set(@NotNull ItemStack holder, @NotNull NamespacedKey key, double value) {
        set(holder, PersistentDataType.DOUBLE, key, value);
    }

    public static void set(@NotNull PersistentDataHolder holder, @NotNull NamespacedKey key, double value) {
        set(holder, PersistentDataType.DOUBLE, key, value);
    }

    public static void set(@NotNull ItemStack holder, @NotNull NamespacedKey key, int value) {
        set(holder, PersistentDataType.INTEGER, key, value);
    }

    public static void set(@NotNull PersistentDataHolder holder, @NotNull NamespacedKey key, int value) {
        set(holder, PersistentDataType.INTEGER, key, value);
    }

    public static void set(@NotNull ItemStack holder, @NotNull NamespacedKey key, long value) {
        set(holder, PersistentDataType.LONG, key, value);
    }

    public static void set(@NotNull PersistentDataHolder holder, @NotNull NamespacedKey key, long value) {
        set(holder, PersistentDataType.LONG, key, value);
    }

    public static void set(@NotNull ItemStack holder, @NotNull NamespacedKey key, @Nullable String value) {
        set(holder, PersistentDataType.STRING, key, value);
    }

    public static void set(@NotNull PersistentDataHolder holder, @NotNull NamespacedKey key, @Nullable String value) {
        set(holder, PersistentDataType.STRING, key, value);
    }

    public static void set(@NotNull ItemStack holder, @NotNull NamespacedKey key, String[] value) {
        set(holder, STRING_ARRAY, key, value);
    }

    public static void set(@NotNull PersistentDataHolder holder, @NotNull NamespacedKey key, String[] value) {
        set(holder, STRING_ARRAY, key, value);
    }

    public static void set(@NotNull ItemStack holder, @NotNull NamespacedKey key, double[] value) {
        set(holder, DOUBLE_ARRAY, key, value);
    }

    public static void set(@NotNull PersistentDataHolder holder, @NotNull NamespacedKey key, double[] value) {
        set(holder, DOUBLE_ARRAY, key, value);
    }

    public static void set(@NotNull ItemStack holder, @NotNull NamespacedKey key, @Nullable UUID value) {
        set(holder, UUID, key, value);
    }

    public static void set(@NotNull PersistentDataHolder holder, @NotNull NamespacedKey key, @Nullable UUID value) {
        set(holder, UUID, key, value);
    }

    public static void set(@NotNull ItemStack holder, @NotNull NamespacedKey key, @Nullable StackableCooldown value) {
        set(holder, STACKABLE_COOLDOWN, key, value);
    }

    public static void set(@NotNull PersistentDataHolder holder, @NotNull NamespacedKey key, @Nullable StackableCooldown value) {
        set(holder, STACKABLE_COOLDOWN, key, value);
    }

    public static <T, Z> void set(
        @NotNull ItemStack item,
        @NotNull PersistentDataType<T, Z> dataType,
        @NotNull NamespacedKey key,
        @Nullable Z value
    ) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        set(meta, dataType, key, value);
        item.setItemMeta(meta);
    }

    public static <T, Z> void set(
        @NotNull PersistentDataHolder holder,
        @NotNull PersistentDataType<T, Z> dataType,
        @NotNull NamespacedKey key,
        @Nullable Z value
    ) {
        if (value == null) {
            remove(holder, key);
            return;
        }

        PersistentDataContainer container = holder.getPersistentDataContainer();
        container.set(key, dataType, value);
    }

    public static void remove(@NotNull ItemStack holder, @NotNull NamespacedKey key) {
        ItemMeta meta = holder.getItemMeta();
        if (meta == null) return;

        remove(meta, key);
    }

    public static void remove(@NotNull PersistentDataHolder holder, @NotNull NamespacedKey key) {
        PersistentDataContainer container = holder.getPersistentDataContainer();
        container.remove(key);

        /*if (holder instanceof BlockState state) {
            state.update();
        }*/
    }

    public static @NotNull Optional<String> getString(@NotNull ItemStack holder, @NotNull NamespacedKey key) {
        return get(holder, PersistentDataType.STRING, key);
    }

    public static @NotNull Optional<String> getString(@NotNull PersistentDataHolder holder, @NotNull NamespacedKey key) {
        return get(holder, PersistentDataType.STRING, key);
    }

    public static @NotNull Optional<String[]> getStringArray(@NotNull ItemStack holder, @NotNull NamespacedKey key) {
        return get(holder, STRING_ARRAY, key);
    }

    public static @NotNull Optional<String[]> getStringArray(@NotNull PersistentDataHolder holder, @NotNull NamespacedKey key) {
        return get(holder, STRING_ARRAY, key);
    }

    public static @NotNull Optional<double[]> getDoubleArray(@NotNull ItemStack holder, @NotNull NamespacedKey key) {
        return get(holder, DOUBLE_ARRAY, key);
    }

    public static @NotNull Optional<double[]> getDoubleArray(@NotNull PersistentDataHolder holder, @NotNull NamespacedKey key) {
        return get(holder, DOUBLE_ARRAY, key);
    }

    public static @NotNull Optional<Integer> getInt(@NotNull ItemStack holder, @NotNull NamespacedKey key) {
        return get(holder, PersistentDataType.INTEGER, key);
    }

    public static @NotNull Optional<Integer> getInt(@NotNull PersistentDataHolder holder, @NotNull NamespacedKey key) {
        return get(holder, PersistentDataType.INTEGER, key);
    }

    public static @NotNull Optional<Long> getLong(@NotNull ItemStack holder, @NotNull NamespacedKey key) {
        return get(holder, PersistentDataType.LONG, key);
    }

    public static @NotNull Optional<Long> getLong(@NotNull PersistentDataHolder holder, @NotNull NamespacedKey key) {
        return get(holder, PersistentDataType.LONG, key);
    }

    public static @NotNull Optional<Double> getDouble(@NotNull ItemStack holder, @NotNull NamespacedKey key) {
        return get(holder, PersistentDataType.DOUBLE, key);
    }

    public static @NotNull Optional<Double> getDouble(@NotNull PersistentDataHolder holder, @NotNull NamespacedKey key) {
        return get(holder, PersistentDataType.DOUBLE, key);
    }

    public static @NotNull Optional<Boolean> getBoolean(@NotNull ItemStack holder, @NotNull NamespacedKey key) {
        return get(holder, PersistentDataType.INTEGER, key).map(i -> i != 0);
    }

    public static @NotNull Optional<Boolean> getBoolean(@NotNull PersistentDataHolder holder, @NotNull NamespacedKey key) {
        return get(holder, PersistentDataType.INTEGER, key).map(i -> i != 0);
    }

    public static @NotNull Optional<UUID> getUUID(@NotNull ItemStack holder, @NotNull NamespacedKey key) {
        return get(holder, UUID, key);
    }

    public static @NotNull Optional<UUID> getUUID(@NotNull PersistentDataHolder holder, @NotNull NamespacedKey key) {
        return get(holder, UUID, key);
    }

    public static @NotNull Optional<Cooldown> getCooldown(@NotNull ItemStack holder, @NotNull NamespacedKey key) {
        return get(holder, COOLDOWN, key);
    }

    public static @NotNull Optional<Cooldown> getCooldown(@NotNull PersistentDataHolder holder, @NotNull NamespacedKey key) {
        return get(holder, COOLDOWN, key);
    }

    public static @NotNull Optional<StackableCooldown> getStackableCooldown(@NotNull ItemStack holder, @NotNull NamespacedKey key) {
        return get(holder, STACKABLE_COOLDOWN, key);
    }

    public static @NotNull Optional<StackableCooldown> getStackableCooldown(@NotNull PersistentDataHolder holder, @NotNull NamespacedKey key) {
        return get(holder, STACKABLE_COOLDOWN, key);
    }

}
