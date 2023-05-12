package cc.mewcraft.mewfishing.nms.player;

import me.lucko.helper.utils.Log;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public final class PlayerAction {
    private PlayerAction() {
        throw new UnsupportedOperationException("This class cannot instantiate");
    }

    public static void doRightClick(@NotNull Player player) {
        InteractionHand hand = player.getInventory().getItemInMainHand().getType().equals(Material.FISHING_ROD)
            ? InteractionHand.MAIN_HAND : player.getInventory().getItemInOffHand().getType().equals(Material.FISHING_ROD)
            ? InteractionHand.OFF_HAND : null;
        if (hand == null) return;
        ServerPlayer serverPlayer;
        try {
            serverPlayer = (ServerPlayer) player.getClass().getMethod("getHandle").invoke(player);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Log.severe(e.getMessage());
            return;
        }
        serverPlayer.gameMode.useItem(serverPlayer, serverPlayer.getLevel(), serverPlayer.getItemInHand(hand), hand);
        serverPlayer.swing(hand, true);
    }

}
