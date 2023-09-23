package cc.mewcraft.worldreset.schedule

import cc.mewcraft.worldreset.data.CronData
import com.cronutils.model.Cron
import com.cronutils.model.time.ExecutionTime
import org.bukkit.plugin.Plugin
import java.time.Duration
import java.time.ZonedDateTime
import kotlin.jvm.optionals.getOrNull

abstract class LocalSchedule(
    protected val plugin: Plugin,
    override val name: String,
    private val cronData: CronData,
) : Schedule {
    override val cron: Cron
        get() = cronData.cron

    override fun timeToNextExecution(): Duration? {
        return ExecutionTime.forCron(cronData.cron).timeToNextExecution(ZonedDateTime.now()).getOrNull()
    }
}
