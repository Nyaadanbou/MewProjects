package cc.mewcraft.pickaxepower.listener;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.mewcore.util.UtilComponent;
import cc.mewcraft.pickaxepower.PickaxePower;
import cc.mewcraft.pickaxepower.PowerResolver;
import com.google.inject.Inject;
import me.lucko.helper.function.chain.Chain;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.component;

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

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        int pickaxePower = powerResolver.resolve(player.getActiveItem());
        int blockPower = powerResolver.resolve(block);

        if (pickaxePower < blockPower) {
            event.setDropItems(false);
            Chain.start(this.plugin.getConfig().getString("messages.not-enough-pickaxe-power"))
                .map(s -> UtilComponent.asComponent(s, component("power", text(blockPower))))
                .apply(player::sendActionBar);
        }
    }

}
