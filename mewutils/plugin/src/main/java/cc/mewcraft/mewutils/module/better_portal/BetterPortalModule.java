package cc.mewcraft.mewutils.module.better_portal;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.mewutils.api.MewPlugin;
import cc.mewcraft.mewutils.api.module.ModuleBase;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import org.bukkit.Location;
import org.bukkit.PortalType;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.Normalizer;

public class BetterPortalModule extends ModuleBase implements AutoCloseableListener {
    double normalScale;
    double netherScale;
    double theEndScale;

    @Inject
    public BetterPortalModule(final MewPlugin parent) {
        super(parent);
    }

    @Override protected void load() throws Exception {
        normalScale = getConfigNode().node("scale").node("normal").getDouble();
        netherScale = getConfigNode().node("scale").node("nether").getDouble();
        theEndScale = getConfigNode().node("scale").node("the_end").getDouble();
    }

    @Override protected void enable() {
        registerListener(this);
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL &&
            event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL
        ) {
            return;
        }

        Location to = event.getTo();
        Location from = event.getFrom();
        Location newTo = findNewTeleportLocation(from, to, event.getPlayer());

        if (newTo != null) {
            event.setTo(newTo);
        }
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
        if (event.getPortalType() != PortalType.NETHER && event.getPortalType() != PortalType.ENDER) {
            return;
        }

        Location fromLocation = event.getFrom().clone();
        Location toLocation = event.getTo();
        Location newTo = findNewTeleportLocation(fromLocation, toLocation, event.getEntity());
        event.setTo(newTo);
    }

    private double getWorldScaling(final World world) {
        return switch (world.getEnvironment()) {
            case NORMAL -> normalScale;
            case NETHER -> netherScale;
            case THE_END -> theEndScale;
            default -> throw new IllegalStateException(world.getName() + " has no scale defined in the config");
        };
    }

    private @Nullable Location findNewTeleportLocation(@NotNull Location from, @Nullable Location target, @Nullable Entity entity) {
        Preconditions.checkNotNull(from, "from");

        if (target == null) {
            return null;
        }

        from = from.clone();

        // Scale the target location
        double fromScaling = getWorldScaling(from.getWorld());
        double toScaling = getWorldScaling(target.getWorld());
        double scaling = fromScaling / toScaling;

        if (entity instanceof Player player) {
            getLang().of("world_scale_tips")
                .resolver(
                    Formatter.number("scale1", fromScaling),
                    Formatter.number("scale2", toScaling)
                )
                .send(player);
        }

        return new Location(
            target.getWorld(),
            from.getX() * scaling,
            from.getY(),
            from.getZ() * scaling
        );
    }
}
