package cc.mewcraft.townylink.impl.packet;

import cc.mewcraft.townylink.api.TownData;
import com.google.common.collect.ImmutableSet;
import me.lucko.helper.messaging.conversation.ConversationMessage;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GlobalTownResponse implements ConversationMessage {
    private final UUID id;
    public final ImmutableSet<TownData> townDataSet;

    public GlobalTownResponse(final UUID id, final ImmutableSet<TownData> townDataSet) {
        this.id = id;
        this.townDataSet = townDataSet;
    }

    @Override public @NotNull UUID getConversationId() {
        return id;
    }

    @Override public String toString() {
        return "GlobalTownResponse{" +
               "id=" + id +
               '}';
    }
}
