package cc.mewcraft.adventurelevel.file;

import cc.mewcraft.adventurelevel.AdventureLevel;

public abstract class AbstractDataStorage implements DataStorage {
    protected final AdventureLevel plugin;

    public AbstractDataStorage(final AdventureLevel plugin) {
        this.plugin = plugin;
    }
}
