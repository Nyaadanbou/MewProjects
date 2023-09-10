package com.cronutils

class ExecutableUnit(
    val job: CronJob,
    val trigger: CronTrigger,
) : Comparable<ExecutableUnit> {
    override fun compareTo(other: ExecutableUnit): Int {
        val thisObj = trigger.nextExecution()
        val otherObj = other.trigger.nextExecution()
        return if (thisObj != null) {
            if (otherObj != null) {
                thisObj.compareTo(otherObj)
            } else 1
        } else -1
    }
}