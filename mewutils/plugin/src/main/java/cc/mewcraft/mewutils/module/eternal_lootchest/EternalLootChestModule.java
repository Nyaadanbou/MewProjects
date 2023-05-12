package cc.mewcraft.mewutils.module.eternal_lootchest;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.mewutils.api.MewPlugin;
import cc.mewcraft.mewutils.api.module.ModuleBase;
import com.google.inject.Inject;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;

public class EternalLootChestModule extends ModuleBase implements AutoCloseableListener {
    @Inject
    public EternalLootChestModule(final MewPlugin parent) {
        super(parent);
    }

    @Override
    protected void enable() throws Exception {
        registerListener(this);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getState() instanceof Chest chest) {
            if (chest.hasLootTable())
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent event) {
        if (event.getBlock().getState() instanceof Chest chest) {
            if (chest.hasLootTable())
                event.setCancelled(true);
        }
    }
}
