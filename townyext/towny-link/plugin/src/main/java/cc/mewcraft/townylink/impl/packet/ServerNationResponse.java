package cc.mewcraft.townylink.impl.packet;

import cc.mewcraft.townylink.api.NationData;
import com.google.common.collect.ImmutableSet;
import me.lucko.helper.messaging.conversation.ConversationMessage;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ServerNationResponse implements ConversationMessage {
    private final UUID id;
    public final String sourceServer;
    public final ImmutableSet<NationData> nationDataSet;

    public ServerNationResponse(
        final UUID id,
        final String sourceServer,
        final ImmutableSet<NationData> nationDataSet
    ) {
        this.id = id;
        this.sourceServer = sourceServer;
        this.nationDataSet = nationDataSet;
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
