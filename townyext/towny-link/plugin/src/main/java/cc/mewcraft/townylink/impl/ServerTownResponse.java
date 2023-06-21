package cc.mewcraft.townylink.impl;

import cc.mewcraft.townylink.api.TownData;
import me.lucko.helper.messaging.conversation.ConversationMessage;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class ServerTownResponse implements ConversationMessage {
    private final UUID id;
    final String sourceServer;
    final List<TownData> townDataList;

    public ServerTownResponse(
        final UUID id,
        final String sourceServer,
        final List<TownData> townDataList
    ) {
        this.id = id;
        this.sourceServer = sourceServer;
        this.townDataList = townDataList;
    }

    @Override public @NotNull UUID getConversationId() {
        return id;
    }

    @Override public String toString() {
        return "ServerTownResponse{" +
               "id=" + id +
               ", sourceServer='" + sourceServer + '\'' +
               '}';
    }
}
