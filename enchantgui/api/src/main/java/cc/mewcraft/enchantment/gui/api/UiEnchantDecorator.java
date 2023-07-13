package cc.mewcraft.enchantment.gui.api;

import net.kyori.adventure.key.Key;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public abstract class UiEnchantDecorator implements UiEnchant {
    protected UiEnchant enchantment;

    public UiEnchantDecorator(final UiEnchant decoratedEnchantment) {
        this.enchantment = decoratedEnchantment;
    }

    @Override public @NotNull String name() {
        return enchantment.name();
    }

    @Override public @NotNull Map<Integer, String> displayName() {
        return enchantment.displayName();
    }

    @Override public @NotNull Map<Integer, List<String>> description() {
        return enchantment.description();
    }

    @Override public boolean canEnchantment(final @NotNull ItemStack item) {
        return enchantment.canEnchantment(item);
    }

    @Override public @NotNull List<UiEnchantTarget> enchantmentTargets() {
        return enchantment.enchantmentTargets();
    }

    @Override public @NotNull UiEnchantRarity rarity() {
        return enchantment.rarity();
    }

    @Override public double enchantingChance() {
        return enchantment.enchantingChance();
    }

    @Override public double villagerTradeChance() {
        return enchantment.villagerTradeChance();
    }

    @Override public double lootGenerationChance() {
        return enchantment.lootGenerationChance();
    }

    @Override public double fishingChance() {
        return enchantment.fishingChance();
    }

    @Override public double mobSpawningChance() {
        return enchantment.mobSpawningChance();
    }

    @Override public @NotNull List<Key> conflict() {
        return enchantment.conflict();
    }

    @Override public boolean conflictsWith(final @NotNull Enchantment other) {
        return enchantment.conflictsWith(other);
    }

    @Override public int minimumLevel() {
        return enchantment.minimumLevel();
    }

    @Override public int maximumLevel() {
        return enchantment.maximumLevel();
    }

    @Override public @NotNull Key key() {
        return enchantment.key();
    }
}
