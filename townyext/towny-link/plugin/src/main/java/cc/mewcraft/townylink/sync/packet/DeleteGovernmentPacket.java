package cc.mewcraft.townylink.sync.packet;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.UUID;

/**
 * This packet is sent whenever:
 * <ul>
 *     <li>a government is deleted from this server</li>
 * </ul>
 */
public record DeleteGovernmentPacket(
    @NonNull String sender,
    @NonNull UUID uuid,
    @NonNull GovernmentType type
) {
    @Override public String toString() {
        return "DeleteGovernmentPacket{" +
               "sender=" + sender +
               ", uuid=" + uuid +
               ", type=" + type +
               '}';
    }
}
