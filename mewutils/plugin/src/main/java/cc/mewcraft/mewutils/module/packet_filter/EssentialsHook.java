package cc.mewcraft.mewutils.module.packet_filter;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import me.lucko.helper.utils.Players;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.event.EventHandler;

import java.util.Objects;

public class EssentialsHook implements AutoCloseableListener {

    private final PacketFilterModule module;

    public EssentialsHook(final PacketFilterModule module) {
        this.module = module;

        Essentials essentials = Objects.requireNonNull(module.getPlugin("Essentials", Essentials.class));
        Players.all().stream()
            .map(essentials::getUser)
            .filter(User::isAfk)
            .map(User::getUUID)
            .forEach(user -> this.module.afkPlayers.add(user));
    }

    @EventHandler
    public void onAfkStatusChange(AfkStatusChangeEvent e) {
        if (e.getValue()) {
            this.module.afkPlayers.add(e.getAffected().getBase().getUniqueId());
            // this.module.info("Added player " + e.getAffected().getName() + " to AFK list");
        } else {
            this.module.afkPlayers.remove(e.getAffected().getBase().getUniqueId());
            // this.module.info("Removed player " + e.getAffected().getName() + " from AFK list");
            // TODO resend nearby entity locations to the player
        }
    }

}
