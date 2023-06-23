package cc.mewcraft.townylink.impl.packet;

import me.lucko.helper.messaging.conversation.ConversationMessage;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerTownRequest implements ConversationMessage {
    private final UUID id;
    public final String serverId;
    public final UUID playerId;

    public PlayerTownRequest(
        final String serverId,
        final UUID playerId
    ) {
        this.id = UUID.randomUUID();
        this.serverId = serverId;
        this.playerId = playerId;
    }

    @Override public @NotNull UUID getConversationId() {
        return id;
    }

    @Override public String toString() {
        return "PlayerTownRequest{" +
               "id=" + id +
               ", serverId=" + serverId +
               ", playerId=" + playerId +
               '}';
    }
}
