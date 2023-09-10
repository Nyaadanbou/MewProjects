@file:Suppress("UnstableApiUsage")

package com.cronutils

import com.google.common.collect.MinMaxPriorityQueue
import java.util.*
import java.util.concurrent.*

class CronScheduler {
    private val poller: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    private val executor: ExecutorService = Executors.newCachedThreadPool()
    private val executingByIds: MutableSet<String> = ConcurrentHashMap.newKeySet()

    private val waiting: Queue<ExecutableUnit> = MinMaxPriorityQueue.maximumSize(250).create()
    private val executing: Queue<ExecutableUnit> = MinMaxPriorityQueue.maximumSize(250).create()
    private val succeeded: Queue<ExecutableUnit> = MinMaxPriorityQueue.maximumSize(250).create()
    private val failed: Queue<ExecutableUnit> = MinMaxPriorityQueue.maximumSize(250).create()

    private val executionTimes: ExecutionTimes = ExecutionTimes()

    private val units: List<ExecutableUnit> = LinkedList() // TODO loop through it at every minute

    /**
     * Starts polling cron job from waiting queue.
     */
    fun startPollingTask() {
        poller.scheduleAtFixedRate(task@{
            val unit = waiting.peek()
            if (unit != null &&
                unit.trigger.nextExecution() != null &&
                unit.trigger.matchNow()
            ) {
                if (unit.job.id !in executingByIds) {
                    if (executionTimes.current(unit))
                        return@task
                    executing.add(unit)
                    executor.execute(unit.job)
                    executingByIds.add(unit.job.id)
                    waiting.remove()
                }
            }
        }, 0, 1, TimeUnit.SECONDS) // TODO Does it mean that up to 60 cron jobs can be scheduled at every minute?
    }

    /**
     * Shutdown the polling tasks and running scheduled tasks.
     */
    fun stopPollingTask() {
        poller.shutdown()
        executor.shutdown()
    }

    fun scheduleCronJob(unit: ExecutableUnit) {
        unit.job.addStatusHook { status: ExecutionStatus ->
            executing.remove(unit)
            executingByIds.remove(unit.job.id)

            when (status) {
                ExecutionStatus.FAILURE -> failed.add(unit)
                ExecutionStatus.SUCCESS -> succeeded.add(unit)
                ExecutionStatus.RUNNING -> throw IllegalArgumentException("RUNNING status is not allowed here")
                ExecutionStatus.WAITING -> throw IllegalArgumentException("WAITING status is not allowed here")
            }

            executionTimes.record(unit)

            if (unit.trigger.nextExecution() != null) {
                waiting.add(unit)
            }
        }

        waiting.add(unit)
    }
}
