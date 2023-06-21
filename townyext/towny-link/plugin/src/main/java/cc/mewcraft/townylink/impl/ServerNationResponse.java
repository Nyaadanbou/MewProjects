package cc.mewcraft.townylink.impl;

import cc.mewcraft.townylink.api.NationData;
import me.lucko.helper.messaging.conversation.ConversationMessage;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class ServerNationResponse implements ConversationMessage {
    private final UUID id;
    final String sourceServer;
    final List<NationData> nationDataList;

    public ServerNationResponse(
        final UUID id,
        final String sourceServer,
        final List<NationData> nationDataList
    ) {
        this.id = id;
        this.sourceServer = sourceServer;
        this.nationDataList = nationDataList;
    }

    @Override public @NotNull UUID getConversationId() {
        return id;
    }

    @Override public String toString() {
        return "ServerNationResponse{" +
               "id=" + id +
               ", sourceServer=" + sourceServer +
               '}';
    }
}
