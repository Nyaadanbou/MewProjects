package cc.mewcraft.townylink.impl;

import cc.mewcraft.townylink.api.TownData;
import me.lucko.helper.messaging.conversation.ConversationMessage;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerTownResponse implements ConversationMessage {
    private final UUID id;
    final String sourceServer;
    final TownData townData;

    public PlayerTownResponse(
        final UUID id,
        final String sourceServer,
        final TownData townData
    ) {
        this.id = id;
        this.sourceServer = sourceServer;
        this.townData = townData;
    }

    @Override public @NotNull UUID getConversationId() {
        return id;
    }

    @Override public String toString() {
        return "PlayerTownResponse{" +
               "id=" + id +
               ", sourceServer='" + sourceServer + '\'' +
               '}';
    }
}
