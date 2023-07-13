package cc.mewcraft.enchantment.gui.gui;

import cc.mewcraft.enchantment.gui.EnchantGuiPlugin;
import cc.mewcraft.enchantment.gui.api.UiEnchant;
import cc.mewcraft.enchantment.gui.api.UiEnchantProvider;
import cc.mewcraft.enchantment.gui.config.EnchantGuiSettings;
import cc.mewcraft.enchantment.gui.util.AdventureUtils;
import com.google.inject.Inject;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EnchantMenu {
    final ItemProviderCache cache;
    final EnchantGuiPlugin plugin;
    final EnchantGuiSettings settings;

    final PagedGui<Item> gui;
    final Window.Builder.Normal.Single window;
    final VirtualInventory compatibilityCheckInventory;
    final VirtualInventory enchantmentLookupInventory;

    final Function<UiEnchant, Item> itemFunction;

    @Inject
    public EnchantMenu(
        final ItemProviderCache cache,
        final EnchantGuiPlugin plugin,
        final EnchantGuiSettings settings,
        final BackItem backItem,
        final ForwardItem forwardItem
    ) {
        this.cache = cache;
        this.plugin = plugin;
        this.settings = settings;

        this.itemFunction = key -> (Item) PreviewItem.withStateChangeHandler(
            (player, state) -> player.playSound(settings.switchSound()), cache.get(key)
        );

        this.compatibilityCheckInventory = new VirtualInventory(1);
        this.enchantmentLookupInventory = new VirtualInventory(1);

        this.gui = PagedGui.items()
            .setStructure(settings.guiLayout())
            .addIngredient('#', new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("")))
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .addIngredient('i', compatibilityCheckInventory)
            .addIngredient('s', enchantmentLookupInventory)
            .addIngredient('<', backItem)
            .addIngredient('>', forwardItem)
            .setContent(UiEnchantProvider.all().map(itemFunction).toList()) // initially, show all enchantments
            .build();

        this.window = Window.single()
            .setTitle(AdventureUtils.translatable("menu.enchantment.title"))
            .setGui(gui);
    }

    public void open(@NotNull Player viewer) {
        setUpdateHandler(viewer);
        window.addOpenHandler(() -> viewer.playSound(settings.openSound()));
        window.addCloseHandler(() -> {
            returnItems(compatibilityCheckInventory, viewer);
            returnItems(enchantmentLookupInventory, viewer);
        });
        window.open(viewer);
    }

    private void setUpdateHandler(Player viewer) {
        compatibilityCheckInventory.setPostUpdateHandler(event -> {

            // Filter out all the enchantments that is applicable to the test item

            ItemStack test = compatibilityCheckInventory.getItem(0);
            List<Item> content;
            if (test != null) {
                viewer.playSound(settings.testSound());
                plugin.getLang().of("msg_filter_out_applicable").resolver(Placeholder.component("item", test.displayName())).send(viewer);
                content = UiEnchantProvider.filter(test).map(itemFunction).toList();
            } else {
                content = UiEnchantProvider.all().map(itemFunction).toList();
            }
            gui.setContent(content);
        });

        enchantmentLookupInventory.setPostUpdateHandler(event -> {

            // Filter out all the enchantments that present in the test item

            ItemStack test = enchantmentLookupInventory.getItem(0);
            List<Item> content;
            if (test != null) {
                viewer.playSound(settings.testSound());
                plugin.getLang().of("msg_filter_out_current").resolver(Placeholder.component("item", test.displayName())).send(viewer);
                ItemMeta itemMeta = test.getItemMeta();
                Set<Key> enchantments = (itemMeta instanceof EnchantmentStorageMeta storageMeta)
                    ? storageMeta.getStoredEnchants().keySet().stream().map(Enchantment::key).collect(Collectors.toSet())
                    : itemMeta.getEnchants().keySet().stream().map(Enchantment::key).collect(Collectors.toSet());
                content = UiEnchantProvider.filter(enchant -> enchantments.contains(enchant.key())).map(itemFunction).toList();
            } else {
                content = UiEnchantProvider.all().map(itemFunction).toList();
            }
            gui.setContent(content);
        });
    }

    private void returnItems(final @NotNull Inventory inventory, final @NotNull Player player) {
        for (final ItemStack item : inventory.getUnsafeItems()) {
            if (item != null) player.getInventory().addItem(item); // must filter out null elements
        }
    }
}
