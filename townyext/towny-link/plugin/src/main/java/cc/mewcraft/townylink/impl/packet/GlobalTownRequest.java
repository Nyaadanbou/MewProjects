package cc.mewcraft.townylink.impl.packet;

import me.lucko.helper.messaging.conversation.ConversationMessage;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GlobalTownRequest implements ConversationMessage {
    private final UUID id;
    public final String senderServer;

    public GlobalTownRequest(final String senderServer) {
        this.id = UUID.randomUUID();
        this.senderServer = senderServer;
    }

    @Override public @NotNull UUID getConversationId() {
        return id;
    }

    @Override public String toString() {
        return "GlobalTownRequest{" +
               "id=" + id +
               ", senderServer=" + senderServer +
               '}';
    }
}
