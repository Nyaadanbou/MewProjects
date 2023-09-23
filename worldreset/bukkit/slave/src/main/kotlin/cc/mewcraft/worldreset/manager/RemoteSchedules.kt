package cc.mewcraft.worldreset.manager

import cc.mewcraft.worldreset.messenger.SlavePluginMessenger
import cc.mewcraft.worldreset.schedule.EmptySchedule
import cc.mewcraft.worldreset.schedule.RemoteSchedule
import cc.mewcraft.worldreset.schedule.Schedule
import cc.mewcraft.worldreset.util.throwUnsupportedException
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import me.lucko.helper.scheduler.HelperExecutors
import java.time.Duration

class RemoteSchedules(
    private val pluginMessenger: SlavePluginMessenger,
) : Schedules {
    private val scheduleCache =
        CacheBuilder.newBuilder()
            .refreshAfterWrite(Duration.ofMinutes(1))
            .expireAfterWrite(Duration.ofMinutes(2))
            .build(CacheLoader.asyncReloading(object : CacheLoader<String, RemoteSchedule>() {
                override fun load(key: String): RemoteSchedule {
                    val scheduleData = pluginMessenger.requestSchedule(key).join()
                    return RemoteSchedule(key, scheduleData.timeToNextExecution)
                }
            }, HelperExecutors.asyncBukkit()))

    override fun start(): Unit =
        throwUnsupportedException()

    override fun get(name: String): Schedule {
        return if (name in scheduleCache.asMap()) {
            scheduleCache[name]
        } else {
            // If the result is not yet cached,
            // we return a temporary empty value
            // and load it at the same time.
            scheduleCache.refresh(name)
            EmptySchedule
        }
    }

    override fun add(schedule: Schedule): Unit =
        throwUnsupportedException()
}