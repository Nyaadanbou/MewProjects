package cc.mewcraft.worldreset.schedule

import com.cronutils.model.Cron
import java.time.Duration

/**
 * Something that will be executed at certain datetime.
 */
interface Schedule {
    val name: String
    val cron: Cron

    /**
     * Executes the task of this schedule.
     */
    suspend fun execute()

    /**
     * Returns the duration to next execution if there is any,
     * or `null` if the next execution can never be reached.
     */
    fun timeToNextExecution(): Duration?
}