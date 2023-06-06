package cc.mewcraft.pickaxepower.listener;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.pickaxepower.PickaxePower;
import cc.mewcraft.pickaxepower.PowerResolver;
import com.google.inject.Inject;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

public class PlayerListener implements AutoCloseableListener {

    private final PickaxePower plugin;
    private final PowerResolver powerResolver;

    @Inject
    public PlayerListener(
        @NonNull PickaxePower plugin,
        @NonNull PowerResolver powerResolver
    ) {
        this.plugin = plugin;
        this.powerResolver = powerResolver;
    }

    // TODO handle explosion?
    // TODO handle piston?

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        int heldItemSlot = player.getInventory().getHeldItemSlot();
        ItemStack heldItem = player.getInventory().getItem(heldItemSlot);
        if (heldItem == null) {
            return;
        }

        int pickaxePower = powerResolver.resolve(heldItem);
        int blockPower = powerResolver.resolve(block);

        if (pickaxePower < blockPower) {
            event.setCancelled(true);
            event.setDropItems(false);
            plugin.getLang().of("msg_not_enough_pickaxe_power")
                .replace("power", blockPower)
                .actionBar(player);
        }
    }

}
