package cc.mewcraft.townylink.sync.packet;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.UUID;

/**
 * This packet is sent whenever:
 * <ul>
 *     <li>a new government is created</li>
 *     <li>a existing government is renamed</li>
 * </ul>
 */
public record NewGovernmentPacket(
    @NonNull String sender,
    @NonNull UUID uuid,
    @NonNull String name,
    @NonNull GovernmentType type
) {
}
