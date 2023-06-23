package cc.mewcraft.townylink.sync.local;

import cc.mewcraft.townylink.TownyLinkPlugin;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class holds Towny data of other Towny servers in the network.
 * <p>
 * Nota that the Towny data of this server should NOT be stored in this class
 * as Towny itself will deal with them (and we should let Towny handle them).
 */
@Singleton
public class GlobalDataHolder {
    private final TownyLinkPlugin plugin;
    private final ConcurrentHashMap<UUID, GovernmentObject> townMap;
    private final ConcurrentHashMap<UUID, GovernmentObject> nationMap;

    @Inject
    public GlobalDataHolder(final TownyLinkPlugin plugin) {
        this.plugin = plugin;
        this.townMap = new ConcurrentHashMap<>();
        this.nationMap = new ConcurrentHashMap<>();
    }

    public void putTown(GovernmentObject data) {
        townMap.put(data.uuid(), data);
    }

    public void putTown(final List<GovernmentObject> data) {
        for (final GovernmentObject datum : data) {
            townMap.put(datum.uuid(), datum);
        }
    }

    public void removeTown(final UUID uuid) {
        townMap.remove(uuid);
    }

    public void putNation(final GovernmentObject data) {
        nationMap.put(data.uuid(), data);
    }

    public void putNation(final List<GovernmentObject> data) {
        for (final GovernmentObject datum : data) {
            nationMap.put(datum.uuid(), datum);
        }
    }

    public void removeNation(final UUID uuid) {
        nationMap.remove(uuid);
    }

    public boolean hasTown(final UUID uuid) {
        return townMap.containsKey(uuid);
    }

    public boolean hasTown(final String townName) {
        return townMap.values().stream().anyMatch(t -> t.name().equalsIgnoreCase(townName));
    }

    public boolean hasNation(final UUID uuid) {
        return nationMap.containsKey(uuid);
    }

    public boolean hasNation(final String nationName) {
        return nationMap.values().stream().anyMatch(t -> t.name().equalsIgnoreCase(nationName));
    }
}
