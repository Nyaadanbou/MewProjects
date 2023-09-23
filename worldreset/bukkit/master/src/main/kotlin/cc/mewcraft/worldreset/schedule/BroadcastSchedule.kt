package cc.mewcraft.worldreset.schedule

import cc.mewcraft.worldreset.data.BroadcastData
import cc.mewcraft.worldreset.data.CronData
import org.bukkit.plugin.Plugin

class BroadcastSchedule(
    plugin: Plugin,
    name: String,
    cron: CronData,
    private val broadcastData: BroadcastData,
) : LocalSchedule(
    plugin,
    name,
    cron
) {
    override suspend fun execute() {
        broadcastData.broadcast()
    }
}
