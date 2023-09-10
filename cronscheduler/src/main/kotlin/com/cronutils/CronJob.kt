package com.cronutils

abstract class CronJob
private constructor(
    val id: String,
) : Runnable {
    private var status = ExecutionStatus.WAITING
    private val statusHooks: MutableList<(ExecutionStatus) -> Unit> = mutableListOf()

    companion object {
        @JvmStatic
        fun create(id: String, action: () -> ExecutionStatus): CronJob =
            object : CronJob(id) {
                override fun execute(): ExecutionStatus =
                    action()
            }
    }

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