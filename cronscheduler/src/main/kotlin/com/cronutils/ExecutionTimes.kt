package com.cronutils

import com.google.common.collect.SortedSetMultimap
import com.google.common.collect.TreeMultimap
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class ExecutionTimes {
    private val executionTimes: SortedSetMultimap<String, ZonedDateTime> = TreeMultimap.create()

    /**
     * Saves current date for the executable unit.
     */
    fun record(unit: ExecutableUnit) {
        executionTimes.put(unit.job.id, ZonedDateTime.now().fuzzy())
    }

    /**
     * Verifies whether the date of last execution for the executable unit is current.
     *
     * Returns true if the date of last execution for the executable unit is current, otherwise false.
     */
    fun current(unit: ExecutableUnit): Boolean =
        executionTimes.get(unit.job.id).lastOrNull() == ZonedDateTime.now().fuzzy()


    private fun ZonedDateTime.fuzzy(): ZonedDateTime =
        this.truncatedTo(ChronoUnit.MINUTES)
}
