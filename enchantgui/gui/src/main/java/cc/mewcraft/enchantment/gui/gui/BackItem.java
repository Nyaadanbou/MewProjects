package cc.mewcraft.enchantment.gui.gui;

import cc.mewcraft.enchantment.gui.config.EnchantGuiSettings;
import cc.mewcraft.enchantment.gui.util.AdventureUtils;
import com.google.inject.Inject;
import org.bukkit.Material;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class BackItem extends AnimatedPageItem {

    @Inject
    public BackItem(EnchantGuiSettings settings) {
        super(settings, false);
    }

    @Override public ItemProvider getItemProvider(final PagedGui<?> gui) {
        ItemBuilder builder = new ItemBuilder(Material.SPECTRAL_ARROW);
        builder.setDisplayName(AdventureUtils.translatable("menu.enchantment.previous_page"));
        return builder;
    }
}
