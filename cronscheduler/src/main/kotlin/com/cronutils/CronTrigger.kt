package com.cronutils

import com.cronutils.model.Cron
import com.cronutils.model.time.ExecutionTime
import java.time.ZonedDateTime
import kotlin.jvm.optionals.getOrNull

class CronTrigger
private constructor(
    private val cron: Cron,
) {
    companion object {
        @JvmStatic
        fun create(cron: Cron): CronTrigger =
            CronTrigger(cron)
    }

    fun matchNow(): Boolean =
        ExecutionTime.forCron(cron).isMatch(ZonedDateTime.now())

    fun nextExecution(): ZonedDateTime? =
        ExecutionTime.forCron(cron).nextExecution(ZonedDateTime.now()).getOrNull()
}