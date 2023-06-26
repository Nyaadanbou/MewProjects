package cc.mewcraft.mewutils.module.eternal_lootchest;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.mewutils.api.MewPlugin;
import cc.mewcraft.mewutils.api.module.ModuleBase;
import com.google.inject.Inject;
import org.bukkit.GameMode;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.loot.Lootable;

public class EternalLootChestModule extends ModuleBase implements AutoCloseableListener {
    @Inject
    public EternalLootChestModule(final MewPlugin parent) {
        super(parent);
    }

    @Override
    protected void enable() throws Exception {
        registerListener(this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getState() instanceof Lootable lootable) {
            if (lootable.hasLootTable()) {
                Player player = event.getPlayer();
                if (player.getGameMode() != GameMode.CREATIVE) {
                    event.setCancelled(true);
                    getLang().of("msg_lootchest_cannot_be_destroyed").send(player);
                } else {
                    getLang().of("msg_lootchest_destroyed_by_creative").send(player);
                }
            }
        }
    }

    /*@EventHandler*/ // Allow chest to be destroyed by explosion
    public void onExplode(BlockExplodeEvent event) {
        if (event.getBlock().getState() instanceof Chest chest) {
            if (chest.hasLootTable()) {
                event.setCancelled(true);
            }
        }
    }
}
