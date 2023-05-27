package cc.mewcraft.adventurelevel.file;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;

public abstract class AbstractDataStorage implements DataStorage {
    protected final AdventureLevelPlugin plugin;

    public AbstractDataStorage(final AdventureLevelPlugin plugin) {
        this.plugin = plugin;
    }
}
