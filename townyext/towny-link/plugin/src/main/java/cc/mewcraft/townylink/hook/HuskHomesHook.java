package cc.mewcraft.townylink.hook;

import cc.mewcraft.townylink.TownyLinkPlugin;
import cc.mewcraft.townylink.teleport.NetworkTeleport;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.lucko.helper.serialize.Point;
import net.william278.huskhomes.api.HuskHomesAPI;
import net.william278.huskhomes.position.Position;
import net.william278.huskhomes.position.World;
import net.william278.huskhomes.teleport.TeleportationException;
import net.william278.huskhomes.user.OnlineUser;
import org.bukkit.entity.Player;

import java.util.UUID;

@Singleton
public class HuskHomesHook implements NetworkTeleport {
    private final TownyLinkPlugin plugin;
    private final HuskHomesAPI huskHomes;

    @Inject
    public HuskHomesHook(final TownyLinkPlugin plugin) {
        this.plugin = plugin;
        this.huskHomes = HuskHomesAPI.getInstance();
    }

    @Override public void teleportPlayer(final Player player, final Point point, final String server) {
        OnlineUser onlineUser = huskHomes.adaptUser(player);
        Position position = Position.at(
            point.getPosition().getX(),
            point.getPosition().getY(),
            point.getPosition().getZ(),
            World.from(point.getPosition().getWorld(), UUID.randomUUID()),
            server
        );

        try {
            huskHomes.teleportBuilder(onlineUser)
                .target(position)
                .toTimedTeleport()
                .execute();
        } catch (TeleportationException e) {
            plugin.getLang().of("msg_teleport_failed_exceptionally").send(player);
            e.printStackTrace();
        }
    }
}
