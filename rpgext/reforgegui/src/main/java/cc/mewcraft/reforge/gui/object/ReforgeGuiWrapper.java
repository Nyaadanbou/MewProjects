package cc.mewcraft.reforge.gui.object;

import cc.mewcraft.reforge.api.ReforgeProvider;
import cc.mewcraft.reforge.gui.ReforgePlugin;
import com.google.inject.Inject;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.item.impl.SimpleItem;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ReforgeGuiWrapper {
    private final ReforgePlugin plugin;
    private final ReforgeConfig config;
    final Gui gui;
    final VirtualInventory inputInv0;
    final VirtualInventory inputInv1;
    final VirtualInventory outputInv;

    @Inject
    public ReforgeGuiWrapper(final ReforgePlugin plugin, final ReforgeConfig config) {
        this.plugin = plugin;
        this.config = config;

        // Initialize inventories
        this.inputInv0 = new VirtualInventory(1);
        this.inputInv1 = new VirtualInventory(10);
        this.outputInv = new VirtualInventory(1);

        // Set GUI priorities so that items go to inputInv0 first when shift-click moving items
        inputInv0.setGuiPriority(2);
        inputInv1.setGuiPriority(1);

        // Stop viewer adding items to outputInv
        outputInv.setPreUpdateHandler(event -> {
            if (!event.isRemove()) event.setCancelled(true);
        });

        // Stop viewer adding items to inputInv0 if outputInv is not empty
        inputInv0.setPreUpdateHandler(event -> {
            if (!outputInv.isEmpty()) event.setCancelled(true);
        });

        String[] layout = plugin.getConfig().getStringList("gui.layout").toArray(String[]::new);
        SimpleItem background = new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE));

        this.gui = Gui.normal()
            .setStructure(layout)
            .addIngredient('#', background)
            .addIngredient('i', inputInv0)
            .addIngredient('u', inputInv1)
            .addIngredient('o', outputInv)
            .addIngredient('c', new ReforgeItem())
            .build();
    }

    private class ReforgeItem extends AbstractItem {
        @Override public ItemProvider getItemProvider() {
            return new ItemBuilder(Material.ANVIL).setDisplayName("Click to reforge!");
        }

        @Override public void handleClick(final @NotNull ClickType clickType, final @NotNull Player player, final @NotNull InventoryClickEvent event) {
            if (clickType.isLeftClick()) {
                // Check if the input inventory has item
                ItemStack item = inputInv0.getItem(0);
                if (item == null) {
                    player.sendMessage("No item in input inventory");
                    return;
                }

                // Check preconditions (e.g. ingredient items, economy currencies)
                if (!config.canReforge(item)) {
                    player.sendMessage("This item cannot be reforged");
                    return;
                }
                List<ReforgeIngredient> ingredients = config.getIngredients(item);
                for (final ReforgeIngredient ingredient : ingredients) {
                    if (ingredient instanceof ItemStackIngredient ii) {
                        if (!ii.has(inputInv1)) {
                            player.sendMessage("You don't have enough items to reforge it");
                            return;
                        }
                    } else if (ingredient instanceof CurrencyIngredient ci) {
                        if (!ci.has(player.getUniqueId())) {
                            player.sendMessage("You don't have enough money to reforge it");
                            return;
                        }
                    } else {
                        player.sendMessage("An internal error occurred on reforge");
                        plugin.getSLF4JLogger().error("An internal error occurred on reforge");
                        return;
                    }
                }

                // Preconditions are met - let's consume ingredients
                for (final ReforgeIngredient ingredient : ingredients) {
                    if (ingredient instanceof ItemStackIngredient ii)
                        ii.consume(inputInv1);
                    else if (ingredient instanceof CurrencyIngredient ci)
                        ci.consume(player.getUniqueId());
                    else
                        return;
                }

                // Ingredients are consumed - refresh the inventory
                inputInv1.notifyWindows();

                // Required ingredients are consumed - let's reforge it
                String option = Objects.requireNonNull(plugin.getConfig().getString("reforge_option"));
                Optional<ItemStack> optional = ReforgeProvider.get().transform(item, option);
                if (optional.isEmpty()) {
                    player.sendMessage("Reforge failed due to an internal error");
                    plugin.getSLF4JLogger().error("An internal error occurred on reforge");
                    return;
                }

                // Set contents of in/out inventories
                inputInv0.setItemSilently(0, null);
                outputInv.setItemSilently(0, optional.get());

                // Play sound when done
                @Subst("minecraft:entity.villager.work_weaponsmith")
                String string = Objects.requireNonNull(plugin.getConfig().getString("reforge_sound.done"));
                player.playSound(Sound.sound(Key.key(string), Sound.Source.MASTER, 1f, 1f));
            }
        }
    }
}