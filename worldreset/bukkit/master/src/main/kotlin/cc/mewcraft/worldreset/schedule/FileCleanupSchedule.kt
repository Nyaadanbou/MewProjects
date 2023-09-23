package cc.mewcraft.worldreset.schedule

import cc.mewcraft.worldreset.data.CronData
import cc.mewcraft.worldreset.data.PathData
import org.bukkit.plugin.Plugin

class FileCleanupSchedule(
    plugin: Plugin,
    name: String,
    cron: CronData,
    private val pathData: PathData,
) : LocalSchedule(
    plugin,
    name,
    cron
) {
    override suspend fun execute() {
        pathData.deleteFiles()
    }
}
