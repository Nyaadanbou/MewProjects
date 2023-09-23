package cc.mewcraft.worldreset.schedule

import cc.mewcraft.worldreset.util.throwUnsupportedException
import java.time.Duration

class RemoteSchedule(
    override val name: String,
    private val timeToNextExecution: Duration?,
) : Schedule {
    override val cron
        get() = throwUnsupportedException()

    override suspend fun execute(): Unit =
        throwUnsupportedException()

    override fun timeToNextExecution(): Duration? =
        timeToNextExecution
}