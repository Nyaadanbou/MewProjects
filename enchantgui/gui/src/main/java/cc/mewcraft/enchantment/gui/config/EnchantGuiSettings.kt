package cc.mewcraft.enchantment.gui.config;

import cc.mewcraft.enchantment.gui.EnchantGuiPlugin;
import com.google.common.base.Suppliers;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.intellij.lang.annotations.Subst;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Singleton
public class EnchantGuiSettings {
    private final EnchantGuiPlugin plugin;
    private final Supplier<Material> itemMaterial;
    private final Supplier<String> displayNameFormat;
    private final Supplier<LoreFormat> loreFormat;
    private final Supplier<String[]> guiLayout;
    private final Supplier<Sound> openSound;
    private final Supplier<Sound> switchSound;
    private final Supplier<Sound> pageTurnSound;
    private final Supplier<Sound> testSound;

    @Inject
    public EnchantGuiSettings(final EnchantGuiPlugin plugin) {
        this.plugin = plugin;

        this.itemMaterial = Suppliers.memoize(() -> {
            String mat = plugin.getConfig().getString("gui.icon.material");
            return Material.matchMaterial(Objects.requireNonNull(mat, "mat"));
        });

        this.displayNameFormat = Suppliers.memoize(() -> plugin.getConfig().getString("gui.icon.name"));

        this.loreFormat = Suppliers.memoize(() -> {
            // Read base lore
            List<String> loreFormat = plugin.getConfig().getStringList("gui.icon.lore");

            // Read sub lists ...
            List<String> chargingFormat = plugin.getConfig().getStringList("gui.lore.charging");
            List<String> conflictFormat = plugin.getConfig().getStringList("gui.lore.conflict");
            List<String> obtainingFormat = plugin.getConfig().getStringList("gui.lore.obtaining");

            return new LoreFormat(
                loreFormat,
                conflictFormat,
                chargingFormat,
                obtainingFormat
            );
        });

        this.guiLayout = Suppliers.memoize(() -> plugin.getConfig().getStringList("gui.layout").toArray(String[]::new));

        this.openSound = Suppliers.memoize(() -> {
            @Subst("minecraft:item.book.page_turn")
            String key = Objects.requireNonNull(plugin.getConfig().getString("sound.open"));
            return Sound.sound(Key.key(key), Sound.Source.MASTER, 1f, 1f);
        });

        this.switchSound = Suppliers.memoize(() -> {
            @Subst("minecraft:item.book.page_turn")
            String key = Objects.requireNonNull(plugin.getConfig().getString("sound.switch"));
            return Sound.sound(Key.key(key), Sound.Source.MASTER, 1f, 1f);
        });

        this.pageTurnSound = Suppliers.memoize(() -> {
            @Subst("minecraft:item.book.page_turn")
            String key = Objects.requireNonNull(plugin.getConfig().getString("sound.page_turn"));
            return Sound.sound(Key.key(key), Sound.Source.MASTER, 1f, 1f);
        });
        this.testSound = Suppliers.memoize(() -> {
            @Subst("minecraft:item.book.page_turn")
            String key = Objects.requireNonNull(plugin.getConfig().getString("sound.test"));
            return Sound.sound(Key.key(key), Sound.Source.MASTER, 1f, 1f);
        });
    }

    public Material itemMaterial() {
        return itemMaterial.get();
    }

    public String displayNameFormat() {
        return displayNameFormat.get();
    }

    public LoreFormat itemLoreFormat() {
        return loreFormat.get();
    }

    public String[] guiLayout() {
        return guiLayout.get();
    }

    public Sound openSound() {
        return openSound.get();
    }

    public Sound switchSound() {
        return switchSound.get();
    }

    public Sound pageTurnSound() {
        return pageTurnSound.get();
    }

    public Sound testSound() {
        return testSound.get();
    }
}
