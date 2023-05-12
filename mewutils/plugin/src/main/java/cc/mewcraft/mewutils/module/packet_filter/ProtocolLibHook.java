package cc.mewcraft.mewutils.module.packet_filter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.concurrency.PacketTypeSet;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.lucko.helper.terminable.Terminable;
import org.bukkit.entity.EntityType;

import java.util.*;

public class ProtocolLibHook implements Terminable {

    private final PacketFilterModule module;
    private final ProtocolManager protocolManager;

    // packet listeners
    private final PacketAdapter packetBlocker;
    private final PacketAdapter entityLogger;

    // packet types that relate to entity
    private final PacketTypeSet entityPackets;

    // ids of entity whose packets should not be blocked
    private final Set<Integer> whitelistEntityIds;

    public ProtocolLibHook(final PacketFilterModule module) {
        this.module = module;
        this.protocolManager = ProtocolLibrary.getProtocolManager();

        this.entityPackets = new PacketTypeSet();
        this.entityPackets.addAll(Arrays.asList(
            PacketType.Play.Server.SPAWN_ENTITY,
            PacketType.Play.Server.SPAWN_ENTITY_EXPERIENCE_ORB,
            PacketType.Play.Server.NAMED_ENTITY_SPAWN,
            PacketType.Play.Server.ENTITY_STATUS,
            PacketType.Play.Server.REL_ENTITY_MOVE,
            PacketType.Play.Server.REL_ENTITY_MOVE_LOOK,
            PacketType.Play.Server.ENTITY_LOOK,
            PacketType.Play.Server.ENTITY_DESTROY,
            PacketType.Play.Server.REMOVE_ENTITY_EFFECT,
            PacketType.Play.Server.ENTITY_HEAD_ROTATION,
            PacketType.Play.Server.ENTITY_METADATA,
            PacketType.Play.Server.ENTITY_VELOCITY,
            PacketType.Play.Server.ENTITY_EQUIPMENT,
            PacketType.Play.Server.ENTITY_SOUND,
            PacketType.Play.Server.ENTITY_TELEPORT,
            PacketType.Play.Server.ENTITY_EFFECT
        ));

        this.whitelistEntityIds = Collections.newSetFromMap(new WeakHashMap<>());

        // --- define packet listeners ---
        this.packetBlocker = new PacketAdapter(
            module.getParentPlugin(),
            ListenerPriority.HIGHEST,
            module.blockedPacketTypes.stream()
                .flatMap(name -> {
                    Collection<PacketType> packetTypes = PacketType.fromName(name);
                    if (packetTypes.isEmpty()) module.warn("Unknown packet type: " + name);
                    return packetTypes.stream();
                })
                .toList()
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                if (ProtocolLibHook.this.entityPackets.contains(packet.getType())) {
                    Integer entityId = packet.getIntegers().readSafely(0);
                    if (entityId != null && ProtocolLibHook.this.whitelistEntityIds.contains(entityId))
                        return; // Let the packet go through if the entity is whitelisted
                }

                if (module.afkPlayers.contains(event.getPlayer().getUniqueId()))
                    event.setCancelled(true); // Don't send this packet to AFK player
            }
        };
        this.entityLogger = new PacketAdapter(
            module.getParentPlugin(),
            ListenerPriority.MONITOR,
            PacketType.Play.Server.SPAWN_ENTITY
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                EntityType entityType = packet.getEntityTypeModifier().read(0);
                if (module.filteredEntityTypes.contains(entityType)) {
                    int entityId = packet.getIntegers().readSafely(0);
                    ProtocolLibHook.this.whitelistEntityIds.add(entityId);
                    // module.info("Add entity id " + entityId + " to whitelist");
                }
            }
        };

        // --- register packet listeners ---
        this.protocolManager.addPacketListener(this.packetBlocker);
        this.protocolManager.addPacketListener(this.entityLogger);
    }

    @Override public void close() {
        this.protocolManager.removePacketListener(this.packetBlocker);
        this.protocolManager.removePacketListener(this.entityLogger);
    }

}
