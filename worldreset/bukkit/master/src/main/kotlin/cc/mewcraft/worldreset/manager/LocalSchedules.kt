package cc.mewcraft.worldreset.manager

import cc.mewcraft.worldreset.PluginSettings
import cc.mewcraft.worldreset.WorldResetPlugin
import cc.mewcraft.worldreset.schedule.Schedule
import cc.mewcraft.worldreset.util.mini
import com.cronutils.CronScheduler
import com.cronutils.ExecutionStatus
import com.github.shynixn.mccoroutine.bukkit.launch
import me.lucko.helper.terminable.Terminable

class LocalSchedules(
    private val plugin: WorldResetPlugin,
    private val settings: PluginSettings,
) : Schedules, Terminable {
    private lateinit var scheduler: CronScheduler
    private lateinit var scheduleMap: Map<String, Schedule>

    override fun start() {
        plugin.componentLogger.info("<aqua>Starting scheduler.".mini())

        scheduler = CronScheduler()
        scheduleMap = settings.schedules.associateBy { it.name }
        scheduleMap.forEach { add(it.value) }

        plugin.componentLogger.info("<aqua>Found ${scheduleMap.size} schedules.".mini())
        plugin.componentLogger.info("<aqua>Attempting to start scheduler.".mini())

        scheduler.startPollingTask()

        plugin.componentLogger.info("<aqua>Scheduler has started! Any errors are reported above.".mini())
    }

    override fun get(name: String): Schedule {
        return scheduleMap[name] ?: throw NullPointerException("No such schedule: $name")
    }

    override fun add(schedule: Schedule) {
        scheduler.scheduleCronJob(schedule.name, schedule.cron) {
            plugin.componentLogger.info("<gold>Cron `${schedule.cron.asString()}` is triggered. Executing schedule: `${schedule.name}`".mini())
            plugin.componentLogger.info("<gold>Starting the schedule as coroutine.".mini())

            plugin.launch {
                schedule.execute()
            }

            plugin.componentLogger.info("<gold>Coroutine has started.".mini())

            ExecutionStatus.SUCCESS
        }
    }

    override fun close() {
        scheduler.stopPollingTask()
    }
}