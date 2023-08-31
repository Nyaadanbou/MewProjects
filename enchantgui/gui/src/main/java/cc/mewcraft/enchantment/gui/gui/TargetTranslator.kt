package cc.mewcraft.enchantment.gui.gui;

import cc.mewcraft.enchantment.gui.EnchantGuiPlugin;
import cc.mewcraft.enchantment.gui.api.UiEnchantTarget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

@Singleton
public class EnchantTargetTranslator {
    private final EnchantGuiPlugin plugin;

    @Inject
    public EnchantTargetTranslator(final EnchantGuiPlugin plugin) {
        this.plugin = plugin;
    }

    public List<String> translate(List<UiEnchantTarget> targets) {
        return targets.stream().map(it -> plugin.getLang().of("item_target_%s".formatted(it.name().toLowerCase())).plain()).toList();
    }

    public String join(String separator, List<UiEnchantTarget> targets) {
        return translate(targets).stream().reduce((t1, t2) -> t1 + separator + t2).orElseThrow();
    }
}
