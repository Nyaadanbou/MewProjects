package cc.mewcraft.enchantment.gui.adapter;

import cc.mewcraft.enchantment.gui.api.UiEnchant;
import cc.mewcraft.enchantment.gui.api.UiEnchantAdapter;
import cc.mewcraft.enchantment.gui.api.UiEnchantTarget;
import com.google.inject.Singleton;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.jetbrains.annotations.NotNull;

// TODO implement vanilla enchantments into the menu
@Singleton
public class MinecraftEnchantAdapter implements UiEnchantAdapter<Enchantment, EnchantmentTarget> {
    @Override public void initialize() {
        throw new UnsupportedOperationException();
    }

    @Override public @NotNull UiEnchant adaptEnchantment(final Enchantment enchantment) {
        throw new UnsupportedOperationException();
    }

    @Override public @NotNull UiEnchantTarget adaptEnchantmentTarget(final EnchantmentTarget enchantmentTarget) {
        throw new UnsupportedOperationException();
    }
}
