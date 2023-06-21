package cc.mewcraft.townylink.impl;

import me.lucko.helper.messaging.conversation.ConversationMessage;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ServerTownRequest implements ConversationMessage {
    private final UUID id;
    final String serverId;

    public ServerTownRequest(final String serverId) {
        this.id = UUID.randomUUID();
        this.serverId = serverId;
    }

    @Override public @NotNull UUID getConversationId() {
        return id;
    }

    @Override public String toString() {
        return "ServerTownRequest{" +
               "id=" + id +
               ", serverId=" + serverId +
               '}';
    }
}
