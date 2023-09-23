package com.cronutils

import java.util.*

abstract class CronJob(
    val id: String,
) : Runnable {
    private var status = ExecutionStatus.WAITING
    private val statusHooks: MutableList<(ExecutionStatus) -> Unit> = LinkedList()

    protected abstract fun execute(): ExecutionStatus

    override fun run() {
        status = ExecutionStatus.RUNNING
        status = execute()
        reportStatus()
    }

    fun addStatusHook(hook: (ExecutionStatus) -> Unit) {
        statusHooks.add(hook)
    }

    private fun reportStatus() {
        statusHooks.forEach {
            it.invoke(status)
        }
    }
}