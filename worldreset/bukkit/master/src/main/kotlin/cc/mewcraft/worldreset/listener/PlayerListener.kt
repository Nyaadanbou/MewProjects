package cc.mewcraft.worldreset.listener

import cc.mewcraft.mewcore.listener.AutoCloseableListener
import cc.mewcraft.worldreset.manager.ServerLock
import cc.mewcraft.worldreset.util.mini
import org.bukkit.event.EventHandler
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

class PlayerListener(
    private val serverLocks: ServerLock,
) : AutoCloseableListener {
    @EventHandler
    fun onLogin(event: AsyncPlayerPreLoginEvent) {
        /* Kick player if server lock is enabled */
        if (serverLocks.isLocked()) event.disallow(
            AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
            "World Reset in Progress".mini()
        )
    }
}