package cc.mewcraft.worldreset.messenger

import cc.mewcraft.worldreset.manager.Schedules
import cc.mewcraft.worldreset.manager.ServerLock
import me.lucko.helper.messaging.Messenger
import me.lucko.helper.messaging.conversation.ConversationReply
import me.lucko.helper.promise.Promise
import kotlin.time.Duration
import kotlin.time.toJavaDuration

/**
 * This messenger should be initialized by the `master` module.
 */
class MasterPluginMessenger(
    messenger: Messenger,
    schedules: Schedules,
    serverLock: ServerLock,
) : BasePluginMessenger(messenger) {

    /* It's the receiver side - add listeners that response the requests */

    init {
        scheduleChannel.newAgent().addListener { _, message ->
            val promise = Promise.empty<GetScheduleResponse>()
            val schedule = schedules.get(message.name)
            val timeToNextExecution = (schedule.timeToNextExecution() ?: Duration.INFINITE).toJavaDuration()
            promise.supply(GetScheduleResponse(message.conversationId, ScheduleData(timeToNextExecution)))
            ConversationReply.ofPromise(promise)
        }
        serverLockChannel.newAgent().addListener { _, message ->
            val promise = Promise.empty<QueryServerLockResponse>()
            val status = serverLock.isLocked()
            promise.supply(QueryServerLockResponse(message.conversationId, status))
            ConversationReply.ofPromise(promise)
        }
    }
}