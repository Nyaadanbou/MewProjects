package cc.mewcraft.mewutils.module.color_palette;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class FurnitureListener implements AutoCloseableListener {

    private final ColorPaletteModule module;

    public FurnitureListener(final ColorPaletteModule module) {
        this.module = module;
    }

    @EventHandler
    public void onInteractFurniture(FurnitureInteractEvent event) {
        Player player = event.getPlayer();
        Entity bukkitEntity = event.getBukkitEntity();
        if (player.isSneaking()) {

            // check access
            if (!PaletteHandler.hasAccess(player, bukkitEntity)) {
                this.module.getLang().of("msg.no_permission").send(player);
                return;
            }

            // handle color palette
            for (final PaletteHandler<? extends Entity> handler : this.module.furnitureHandlers) {
                if (handler.canHandle(bukkitEntity)) {
                    handler.startEdit(player, bukkitEntity);
                    return;
                }
            }

            this.module.getLang().of("msg.unsupported_operation").send(player);
        }
    }

}
