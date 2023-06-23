package cc.mewcraft.mewcore.listener;

import me.lucko.helper.terminable.Terminable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public interface AutoCloseableListener extends Listener, Terminable {
    @Override default void close() {
        HandlerList.unregisterAll(this);
    }
}
