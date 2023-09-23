package cc.mewcraft.worldreset.schedule

import cc.mewcraft.worldreset.util.throwUnsupportedException
import com.cronutils.model.Cron
import java.time.Duration

object EmptySchedule : Schedule {
    override val name: String
        get() = "EMPTY"
    override val cron: Cron
        get() = throwUnsupportedException()

    override suspend fun execute(): Unit =
        throwUnsupportedException()

    override fun timeToNextExecution(): Duration =
        Duration.ZERO
}