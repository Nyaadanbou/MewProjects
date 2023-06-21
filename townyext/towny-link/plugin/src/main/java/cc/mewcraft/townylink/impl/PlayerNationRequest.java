package cc.mewcraft.townylink.impl;

import me.lucko.helper.messaging.conversation.ConversationMessage;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerNationRequest implements ConversationMessage {
    private final UUID id;
    final String serverId;
    final UUID playerId;

    public PlayerNationRequest(final String serverId, final UUID playerId) {
        this.id = UUID.randomUUID();
        this.serverId = serverId;
        this.playerId = playerId;
    }

    @Override public @NotNull UUID getConversationId() {
        return id;
    }

    @Override public String toString() {
        return "PlayerNationRequest{" +
               "id=" + id +
               ", serverId='" + serverId + '\'' +
               ", playerId=" + playerId +
               '}';
    }
}
