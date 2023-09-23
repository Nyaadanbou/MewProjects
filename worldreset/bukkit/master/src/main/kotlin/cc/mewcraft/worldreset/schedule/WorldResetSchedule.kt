package cc.mewcraft.worldreset.schedule

import cc.mewcraft.worldreset.data.CommandData
import cc.mewcraft.worldreset.data.CronData
import com.github.shynixn.mccoroutine.bukkit.ticks
import kotlinx.coroutines.delay
import org.bukkit.plugin.Plugin

class WorldResetSchedule(
    plugin: Plugin,
    name: String,
    cron: CronData,
    private val world: String,
    private val keepSeed: Boolean,
    private val preCommandData: CommandData,
    private val postCommandData: CommandData,
) : LocalSchedule(
    plugin,
    name,
    cron
) {
    override suspend fun execute() {
        plugin.server.getWorld(world) ?: run {
            plugin.componentLogger.error("The world `$world` is not found. Aborting execution.")
            return
        }

        // Run pre commands
        plugin.componentLogger.info("Start running pre commands.")
        preCommandData.dispatchAll()
        delay(1.ticks)

        // Reset the world
        plugin.componentLogger.info("Starting world reset process: `$world`. The server might lag for a while.")
        resetWorld()
        plugin.componentLogger.info("The world reset process for `$world` has completed!")

        // Run post commands
        delay(1.ticks)
        plugin.componentLogger.info("Start running post commands.")
        postCommandData.dispatchAll()
    }

    private fun resetWorld() {
        // Get current world instance
        val world = plugin.server.getWorld(world)!!

        // Get current world seed
        val oldSeed = world.seed

        if (keepSeed) {
        }

        // TODO
    }
}
