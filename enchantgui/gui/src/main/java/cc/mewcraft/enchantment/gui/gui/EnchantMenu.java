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
import xyz.xenondevs.invui.animation.impl.RandomAnimation;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.SlotElement;
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
        // Filter out all the enchantments that is applicable to the test item
        compatibilityCheckInventory.setPostUpdateHandler(event -> filterEnchants(compatibilityCheckInventory, test -> {
            viewer.playSound(settings.testSound());
            plugin.getLang().of("msg_filter_out_applicable").resolver(Placeholder.component("item", test.displayName())).send(viewer);
            return UiEnchantProvider.filter(enchant -> enchant.canEnchantment(test)).map(itemFunction).toList();
        }));

        // Filter out all the enchantments that present in the test item
        enchantmentLookupInventory.setPostUpdateHandler(event -> filterEnchants(enchantmentLookupInventory, test -> {
            viewer.playSound(settings.testSound());
            plugin.getLang().of("msg_filter_out_current").resolver(Placeholder.component("item", test.displayName())).send(viewer);

            Set<Key> enchantments;
            ItemMeta itemMeta = test.getItemMeta();
            if (itemMeta instanceof EnchantmentStorageMeta storageMeta) {
                enchantments = storageMeta.getStoredEnchants().keySet().stream().map(Enchantment::key).collect(Collectors.toSet());
            } else {
                enchantments = itemMeta.getEnchants().keySet().stream().map(Enchantment::key).collect(Collectors.toSet());
            }

            return UiEnchantProvider.filter(enchant -> enchantments.contains(enchant.key())).map(itemFunction).toList();
        }));
    }

    private void returnItems(final @NotNull Inventory inventory, final @NotNull Player player) {
        for (final ItemStack item : inventory.getUnsafeItems()) {
            if (item != null) player.getInventory().addItem(item); // must filter out null elements
        }
    }

    private void filterEnchants(final VirtualInventory inventory, final Function<ItemStack, List<Item>> action) {
        ItemStack test = inventory.getItem(0);

        List<Item> content;
        if (test != null) {
            content = action.apply(test);
        } else {
            content = UiEnchantProvider.all().map(itemFunction).toList();
        }

        gui.setContent(content);

        if (test != null) gui.playAnimation(
            new RandomAnimation(1, false),
            it -> (it instanceof SlotElement.ItemSlotElement element) && (element.getItem() instanceof PreviewItem)
        );
    }
}
