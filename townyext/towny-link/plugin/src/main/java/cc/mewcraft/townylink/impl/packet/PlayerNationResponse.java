package cc.mewcraft.townylink.impl.packet;

import cc.mewcraft.townylink.api.NationData;
import me.lucko.helper.messaging.conversation.ConversationMessage;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerNationResponse implements ConversationMessage {
    private final UUID id;
    public final String sourceServer;
    public final NationData nationData;

    public PlayerNationResponse(
        final UUID id,
        final String sourceServer,
        final NationData nationData
    ) {
        this.id = id;
        this.sourceServer = sourceServer;
        this.nationData = nationData;
    }

    @Override public @NotNull UUID getConversationId() {
        return id;
    }

    @Override public String toString() {
        return "PlayerNationResponse{" +
               "id=" + id +
               ", sourceServer=" + sourceServer +
               '}';
    }
}
