package cc.mewcraft.townylink.impl.packet;

import cc.mewcraft.townylink.api.NationData;
import com.google.common.collect.ImmutableSet;
import me.lucko.helper.messaging.conversation.ConversationMessage;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GlobalNationResponse implements ConversationMessage {
    private final UUID id;
    public final ImmutableSet<NationData> nationDataSet;

    public GlobalNationResponse(final UUID id, final ImmutableSet<NationData> nationDataSet) {
        this.id = id;
        this.nationDataSet = nationDataSet;
    }

    @Override public @NotNull UUID getConversationId() {
        return id;
    }

    @Override public String toString() {
        return "GlobalNationResponse{" +
               "id=" + id +
               '}';
    }
}
