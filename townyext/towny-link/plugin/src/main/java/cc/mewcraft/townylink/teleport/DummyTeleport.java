package cc.mewcraft.townylink.teleport;

import cc.mewcraft.townylink.TownyLinkPlugin;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.lucko.helper.serialize.Point;
import org.bukkit.entity.Player;

@Singleton
public class DummyTeleport implements NetworkTeleport {
    private final TownyLinkPlugin plugin;

    @Inject
    public DummyTeleport(final TownyLinkPlugin plugin) {
        this.plugin = plugin;
    }

    @Override public void teleportPlayer(final Player player, final Point point, final String server) {
        plugin.getSLF4JLogger().warn("Cannot teleport player across the network because no implementation is provided");
    }
}
