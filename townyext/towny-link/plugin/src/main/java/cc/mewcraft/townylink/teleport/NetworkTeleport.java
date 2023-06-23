package cc.mewcraft.townylink.teleport;

import me.lucko.helper.serialize.Point;
import org.bukkit.entity.Player;

public interface NetworkTeleport {
    void teleportPlayer(Player player, Point point, String server);
}
