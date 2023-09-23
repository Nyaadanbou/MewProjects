package cc.mewcraft.worldreset

import cc.mewcraft.mewcore.plugin.MeowJavaPlugin
import cc.mewcraft.worldreset.manager.RemoteSchedules
import cc.mewcraft.worldreset.manager.RemoteServerLock
import cc.mewcraft.worldreset.manager.Schedules
import cc.mewcraft.worldreset.manager.ServerLock
import cc.mewcraft.worldreset.messenger.SlavePluginMessenger
import cc.mewcraft.worldreset.placeholder.MiniPlaceholderExtension
import cc.mewcraft.worldreset.placeholder.PlaceholderAPIExtension
import me.lucko.helper.Services
import me.lucko.helper.messaging.Messenger

class WorldResetPlugin : MeowJavaPlugin() {
    private lateinit var schedules: Schedules
    private lateinit var serverLocks: ServerLock

    override fun enable() {
        /* Get the instance of messenger */
        val messenger = Services.load(Messenger::class.java)
        val slavePluginMessenger = SlavePluginMessenger(messenger).apply { bind(this) }

        /* Initialize managers */
        serverLocks = RemoteServerLock(slavePluginMessenger)
        schedules = RemoteSchedules(slavePluginMessenger)

        /* Register expansions */
        MiniPlaceholderExtension(schedules, serverLocks).also { bind(it).register() }
        PlaceholderAPIExtension(schedules, serverLocks).also { bind(it).register() }
    }
}
