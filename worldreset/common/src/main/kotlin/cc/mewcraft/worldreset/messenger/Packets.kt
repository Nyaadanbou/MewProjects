package cc.mewcraft.worldreset.messenger

import me.lucko.helper.messaging.conversation.ConversationMessage
import java.time.Duration
import java.util.*

/* Custom packet fields */

data class ScheduleData(
    val timeToNextExecution: Duration,
)

/* Packet types */

class GetScheduleRequest(
    val name: String,
) : ConversationMessage {
    private val id = UUID.randomUUID()
    override fun getConversationId(): UUID =
        id
}

class GetScheduleResponse(
    private val conversationId: UUID,
    val scheduleData: ScheduleData,
) : ConversationMessage {
    override fun getConversationId(): UUID =
        conversationId
}

/* QueryServerLock */

class QueryServerLockRequest : ConversationMessage {
    private val id = UUID.randomUUID()
    override fun getConversationId(): UUID =
        id
}

class QueryServerLockResponse(
    private val conversationId: UUID,
    val status: Boolean,
) : ConversationMessage {
    override fun getConversationId(): UUID =
        conversationId
}