package cc.mewcraft.worldreset.manager

import cc.mewcraft.worldreset.WorldResetPlugin
import kotlinx.atomicfu.atomic

class LocalServerLock(
    private val plugin: WorldResetPlugin,
) : ServerLock {
    private val lock = atomic(false) /* true = lock is enabled */

    override fun lock() {
        lock.value = true
    }

    override fun unlock() {
        lock.value = false
    }

    override fun setLock(status: Boolean) {
        lock.value = status
    }

    override fun isLocked(): Boolean {
        return lock.value
    }
}