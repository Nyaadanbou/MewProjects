package cc.mewcraft.townylink.impl.packet;

import cc.mewcraft.townylink.api.TownData;
import com.google.common.collect.ImmutableSet;
import me.lucko.helper.messaging.conversation.ConversationMessage;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ServerTownResponse implements ConversationMessage {
    private final UUID id;
    public final String sourceServer;
    public final ImmutableSet<TownData> townDataSet;

    public ServerTownResponse(
        final UUID id,
        final String sourceServer,
        final ImmutableSet<TownData> townDataSet
    ) {
        this.id = id;
        this.sourceServer = sourceServer;
        this.townDataSet = townDataSet;
    }

    @Override public @NotNull UUID getConversationId() {
        return id;
    }

    @Override public String toString() {
        return "ServerTownResponse{" +
               "id=" + id +
               ", sourceServer=" + sourceServer +
               '}';
    }
}
