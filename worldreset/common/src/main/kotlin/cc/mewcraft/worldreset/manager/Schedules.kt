package cc.mewcraft.worldreset.manager

import cc.mewcraft.worldreset.schedule.Schedule

/**
 * This class keeps all the schedules.
 */
interface Schedules {
    fun start()
    fun get(name: String): Schedule
    fun add(schedule: Schedule)
}