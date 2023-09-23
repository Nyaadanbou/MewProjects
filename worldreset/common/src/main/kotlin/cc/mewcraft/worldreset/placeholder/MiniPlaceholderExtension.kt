package cc.mewcraft.worldreset.placeholder

import cc.mewcraft.worldreset.manager.Schedules
import cc.mewcraft.worldreset.manager.ServerLock
import cc.mewcraft.worldreset.util.DurationFormatter
import io.github.miniplaceholders.api.Expansion
import me.lucko.helper.terminable.Terminable
import net.kyori.adventure.text.minimessage.tag.Tag

/* Constant Tags */
private val LOCKED: Tag = Tag.preProcessParsed("LOCKED")
private val UNLOCKED: Tag = Tag.preProcessParsed("UNLOCKED")
private val NEVER_REACH: Tag = Tag.preProcessParsed("NEVER REACH")

class MiniPlaceholderExtension(
    private val schedules: Schedules,
    private val serverLocks: ServerLock,
) : Terminable {
    private val expansion: Expansion = Expansion
        .builder("worldreset")
        .audiencePlaceholder("countdown") { _, queue, _ ->
            val name = queue.pop().value()
            val schedule = schedules.get(name)
            val timeToNextExecution = schedule.timeToNextExecution() ?: return@audiencePlaceholder NEVER_REACH
            val format = DurationFormatter.MINUTES.format(timeToNextExecution)
            Tag.preProcessParsed(format)
        }
        .audiencePlaceholder("serverlock") { _, _, _ ->
            if (serverLocks.isLocked()) {
                LOCKED
            } else {
                UNLOCKED
            }
        }
        .build()

    fun register() {
        expansion.register()
    }

    override fun close() {
        expansion.unregister()
    }
}