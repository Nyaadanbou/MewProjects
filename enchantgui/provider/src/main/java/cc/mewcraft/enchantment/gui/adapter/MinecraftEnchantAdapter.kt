package cc.mewcraft.enchantment.gui.adapter;

import cc.mewcraft.enchantment.gui.api.UiEnchant;
import cc.mewcraft.enchantment.gui.api.UiEnchantAdapter;
import cc.mewcraft.enchantment.gui.api.UiEnchantPlugin;
import cc.mewcraft.enchantment.gui.api.UiEnchantTarget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.jetbrains.annotations.NotNull;

// TODO implement vanilla enchantments into the menu
@Singleton
public class MinecraftEnchantAdapter implements UiEnchantAdapter<Enchantment, EnchantmentTarget> {
    private final UiEnchantPlugin plugin;

    @Inject
    public MinecraftEnchantAdapter(final UiEnchantPlugin plugin) {
        this.plugin = plugin;
    }

    @Override public void initialize() {

    }

    @Override public boolean canInitialize() {
        return true; // vanilla enchantments are always available
    }

    @Override public @NotNull UiEnchant adaptEnchantment(final @NotNull Enchantment enchantment) {
        throw new UnsupportedOperationException();
    }

    @Override public @NotNull UiEnchantTarget adaptEnchantmentTarget(final @NotNull EnchantmentTarget enchantmentTarget) {
        throw new UnsupportedOperationException();
    }
}
