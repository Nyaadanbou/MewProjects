package cc.mewcraft.worldreset.messenger

import me.lucko.helper.messaging.Messenger
import me.lucko.helper.messaging.conversation.ConversationReplyListener
import me.lucko.helper.promise.Promise
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * This messenger should be initialized by the `slave` module.
 */
class SlavePluginMessenger(
    messenger: Messenger,
) : BasePluginMessenger(
    messenger
) {

    /* It's the sending side - add functions that send the requests */

    fun requestSchedule(name: String): Promise<ScheduleData> {
        val promise = Promise.empty<ScheduleData>()

        scheduleChannel.sendMessage(
            GetScheduleRequest(name),
            object : ConversationReplyListener<GetScheduleResponse> {
                override fun onReply(reply: GetScheduleResponse): ConversationReplyListener.RegistrationAction {
                    promise.supply(reply.scheduleData)
                    return ConversationReplyListener.RegistrationAction.STOP_LISTENING
                }

                override fun onTimeout(replies: MutableList<GetScheduleResponse>) {
                    promise.supplyException(TimeoutException("Did not receive response in 1 second"))
                }
            }, 1, TimeUnit.SECONDS
        )

        return promise
    }

    fun queryServerLock(): Promise<Boolean> {
        val promise = Promise.empty<Boolean>()

        serverLockChannel.sendMessage(
            QueryServerLockRequest(),
            object : ConversationReplyListener<QueryServerLockResponse> {
                override fun onReply(reply: QueryServerLockResponse): ConversationReplyListener.RegistrationAction {
                    promise.supply(reply.status)
                    return ConversationReplyListener.RegistrationAction.STOP_LISTENING
                }

                override fun onTimeout(replies: MutableList<QueryServerLockResponse>) {
                    promise.supplyException(TimeoutException("Did not receive response in 1 second"))
                }
            }, 1, TimeUnit.SECONDS
        )

        return promise
    }
}