package cc.mewcraft.worldreset

import cc.mewcraft.mewcore.plugin.MeowJavaPlugin
import cc.mewcraft.worldreset.command.PluginCommands
import cc.mewcraft.worldreset.listener.PlayerListener
import cc.mewcraft.worldreset.listener.WorldListener
import cc.mewcraft.worldreset.manager.LocalSchedules
import cc.mewcraft.worldreset.manager.LocalServerLock
import cc.mewcraft.worldreset.manager.Schedules
import cc.mewcraft.worldreset.manager.ServerLock
import cc.mewcraft.worldreset.messenger.MasterPluginMessenger
import cc.mewcraft.worldreset.placeholder.MiniPlaceholderExtension
import cc.mewcraft.worldreset.placeholder.PlaceholderAPIExtension
import me.lucko.helper.Schedulers
import me.lucko.helper.Services
import me.lucko.helper.messaging.Messenger

class WorldResetPlugin : MeowJavaPlugin() {
    private lateinit var settings: PluginSettings
    private lateinit var schedules: Schedules
    private lateinit var serverLocks: ServerLock

    override fun enable() {
        /* Initialize managers */
        settings = PluginSettings(this)
        serverLocks = LocalServerLock(this)
        schedules = LocalSchedules(this, settings).apply { bind(this) }

        /* Initialize messenger */
        val messenger = Services.load(Messenger::class.java)
        MasterPluginMessenger(messenger, schedules, serverLocks).bindWith(this)

        /* Register listeners */
        PlayerListener(serverLocks).bindWith(this)
        WorldListener(serverLocks).bindWith(this)

        /* Register expansions */
        MiniPlaceholderExtension(schedules, serverLocks).also { bind(it).register() }
        PlaceholderAPIExtension(schedules, serverLocks).also { bind(it).register() }

        /* Register commands */
        PluginCommands(this, serverLocks).prepareAndRegister()

        // Start schedules after "Done"
        Schedulers.bukkit().scheduleSyncDelayedTask(this) { schedules.start() }
    }
}
