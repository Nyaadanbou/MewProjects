package cc.mewcraft.townylink.object;

import cc.mewcraft.townylink.TownyLink;
import com.google.inject.Inject;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TownyRepository {

    private final TownyLink plugin;
    private final Set<String> townNames;
    private final Set<String> nationNames;

    @Inject
    public TownyRepository(final TownyLink plugin) {
        this.plugin = plugin;
        this.townNames = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.nationNames = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    public void addTownName(final String name) {
        this.townNames.add(name.toLowerCase(Locale.ROOT));
    }

    public void addAllTownNames(final List<String> townNames) {
        for (final String townName : townNames) addTownName(townName);
    }

    public void removeTownName(final String name) {
        this.townNames.remove(name.toLowerCase(Locale.ROOT));
    }

    public void removeAllTownName(final List<String> townNames) {
        for (final String townName : townNames) removeTownName(townName);
    }

    public void addNationName(final String name) {
        this.nationNames.add(name.toLowerCase(Locale.ROOT));
    }

    public void addAllNationNames(final List<String> nationNames) {
        for (final String nationName : nationNames) addNationName(nationName);
    }

    public void removeNationName(final String name) {
        this.nationNames.remove(name.toLowerCase(Locale.ROOT));
    }

    public void removeAllNationNames(final List<String> nationNames) {
        for (final String nationName : nationNames) removeNationName(nationName);
    }

    public boolean hasTownName(final String name) {
        return this.townNames.contains(name.toLowerCase(Locale.ROOT));
    }

    public boolean hasNationName(final String name) {
        return this.nationNames.contains(name.toLowerCase(Locale.ROOT));
    }

}
