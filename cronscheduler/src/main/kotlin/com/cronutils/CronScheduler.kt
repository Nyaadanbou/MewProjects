@file:Suppress("UnstableApiUsage")

package com.cronutils

import com.cronutils.model.Cron
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.*

class CronScheduler {
    private val poller: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val executor: ExecutorService = ThreadPoolExecutor(0, 100, 120, TimeUnit.SECONDS, LinkedBlockingQueue())
    private val executing: MutableSet<String> = ConcurrentHashMap.newKeySet()
    private val units: MutableList<ExecutableUnit> = LinkedList()

    /**
     * Starts polling cron jobs.
     *
     * The polling will run once at every minute. For each polling, it loops through all the cron jobs and executes them
     * if current date is matched with the cron date. If the cron date is impossible to reach, it will be removed from
     * the job list permanently.
     */
    fun startPollingTask() {
        poller.scheduleAtFixedRate({
            val now = ZonedDateTime.now()
            val iterator = units.listIterator()
            while (iterator.hasNext()) {
                val next = iterator.next()

                if (next.trigger.nextExecution() == null) {
                    // If the cron can NEVER reach, simply remove it from job list.
                    iterator.remove()
                    continue
                }

                if (next.job.id !in executing && next.trigger.matchTime(now)) {
                    executor.execute(next.job)
                    executing.add(next.job.id)
                }
            }
        }, 0, 1, TimeUnit.MINUTES)
    }

    /**
     * Shutdown the polling task and running scheduled tasks.
     */
    fun stopPollingTask() {
        poller.shutdown()
        executor.shutdown()
    }

    fun scheduleCronJob(name: String, cron: Cron, action: () -> ExecutionStatus) {
        val tri = CronTrigger(cron)
        val job = object : CronJob(name) {
            override fun execute(): ExecutionStatus {
                return action()
            }
        }
        val unit = ExecutableUnit(tri, job).apply {
            this.job.addStatusHook {
                executing.remove(this.job.id)
            }
        }

        units.add(unit)
    }
}
