package cc.mewcraft.mewutils.module.packet_filter;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.mewutils.api.MewPlugin;
import cc.mewcraft.mewutils.api.module.ModuleBase;
import com.google.inject.Inject;
import org.bukkit.entity.EntityType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PacketFilterModule extends ModuleBase implements AutoCloseableListener {

    Set<UUID> afkPlayers; // players who are afk-ing
    EnumSet<EntityType> filteredEntityTypes; // allowed entity packet types
    Set<String> blockedPacketTypes; // names of packet type to be blocked

    @Inject
    public PacketFilterModule(final MewPlugin parent) {
        super(parent);
    }

    @Override protected void load() throws Exception {
        // Initialize member fields
        this.afkPlayers = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.filteredEntityTypes = EnumSet.noneOf(EntityType.class);
        this.blockedPacketTypes = new HashSet<>();

        // Read the config values
        getConfigNode().node("whitelistEntities").getList(String.class, List.of())
            .stream()
            .map(EntityType::valueOf)
            .forEach(entity -> {
                this.filteredEntityTypes.add(entity);
                info("Added whitelisted entity type: " + entity);
            });
        getConfigNode().node("blockedPackets").getList(String.class, List.of())
            .forEach(type -> {
                info("Added blocked packet type: " + type);
                this.blockedPacketTypes.add(type);
            });
    }

    @Override protected void postLoad() {
        registerListener(new EssentialsHook(this));
        new ProtocolLibHook(this).bindWith(this);
    }

    @Override public boolean checkRequirement() {
        return isPluginPresent("Essentials") && isPluginPresent("ProtocolLib");
    }

}
