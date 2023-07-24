package cc.mewcraft.pickaxepower.listener;

import cc.mewcraft.pickaxepower.LoreWriter;
import cc.mewcraft.pickaxepower.PickaxePower;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.google.inject.Inject;
import me.lucko.helper.terminable.Terminable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class PacketListener implements Terminable {
    private final PickaxePower plugin;
    private final LoreWriter writer;

    @Inject
    public PacketListener(@NonNull PickaxePower plugin, @NonNull LoreWriter writer) {
        this.plugin = plugin;
        this.writer = writer;
        this.initialize();
    }

    private void initialize() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();

        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.LOWEST, PacketType.Play.Client.SET_CREATIVE_SLOT) {
            // modifying the items received from creative players
            @Override
            public void onPacketReceiving(final PacketEvent event) {
                PacketContainer packet = event.getPacket();
                ItemStack item = packet.getItemModifier().read(0);
                ItemStack reverted = writer.revert(item);
                packet.getItemModifier().write(0, reverted);
            }
        });

        // try to write the lore at the very bottom by setting to HIGHEST
        ListenerPriority priority = ListenerPriority.HIGHEST;

        manager.addPacketListener(new PacketAdapter(plugin, priority, PacketType.Play.Server.SET_SLOT) {
            // write lore when the server sets an item in a slot
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                ItemStack item = packet.getItemModifier().read(0);
                ItemStack updated = writer.update(item);
                packet.getItemModifier().write(0, updated);
            }
        });

        manager.addPacketListener(new PacketAdapter(plugin, priority, PacketType.Play.Server.WINDOW_ITEMS) {
            // write lore when the server sets a window of items
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                List<ItemStack> items = packet.getItemListModifier().readSafely(0);
                items.replaceAll(writer::update);
                packet.getItemListModifier().write(0, items);
            }
        });

        manager.addPacketListener(new PacketAdapter(plugin, priority, PacketType.Play.Server.OPEN_WINDOW_MERCHANT) {
            // write lore when the server sends the merchant recipes
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                List<MerchantRecipe> list = new ArrayList<>();
                packet.getMerchantRecipeLists().read(0).forEach(recipe -> {
                    ItemStack result = writer.update(recipe.getResult());
                    if (result == null) return;

                    MerchantRecipe recipe2 = new MerchantRecipe(
                        result,
                        recipe.getUses(),
                        recipe.getMaxUses(),
                        recipe.hasExperienceReward(),
                        recipe.getVillagerExperience(),
                        recipe.getPriceMultiplier(),
                        recipe.getDemand(),
                        recipe.getSpecialPrice()
                    );
                    recipe2.setIngredients(recipe.getIngredients());
                    list.add(recipe2);
                });
                packet.getMerchantRecipeLists().write(0, list);
            }
        });
    }

    @Override
    public void close() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(plugin);
    }
}
