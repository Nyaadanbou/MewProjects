package cc.mewcraft.townylink.sync.packet;

import cc.mewcraft.townylink.sync.local.GovernmentObject;
import com.google.common.collect.ImmutableSet;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This packet is sent after the server startup to broadcast
 * all existing towns/nations to other servers in the network.
 */
public record InitialGovernmentPacket(
    @NonNull String sender,
    @NonNull GovernmentType type,
    @NonNull ImmutableSet<GovernmentObject> data
) {
    @Override public String toString() {
        return "InitialGovernmentPacket{" +
               "sender=" + sender +
               ", type=" + type +
               '}';
    }
}
