package cc.mewcraft.adventurelevel.data;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import cc.mewcraft.adventurelevel.level.category.Level;
import cc.mewcraft.adventurelevel.level.category.LevelCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class RealPlayerData implements PlayerData {
    /**
     * The plugin.
     */
    private final AdventureLevelPlugin plugin;
    /**
     * The player's UUID.
     */
    private final UUID uuid;
    /**
     * A map containing all levels.
     */
    private final ConcurrentHashMap<LevelCategory, Level> levelMap;
    /**
     * A variable indicating whether this player data has been fully loaded. If true (=complete), that means the data
     * has been fully loaded, and getters will return current values; otherwise, false (=incomplete) means it's not been
     * fully loaded and the returned values should not be used.
     */
    private final AtomicBoolean complete = new AtomicBoolean(false);

    /**
     * This constructor is used to fast create an empty PlayerData in the main thread.
     * <p>
     * An async callback is meant to be used to update its states at a later point of time.
     *
     * @param plugin the plugin instance
     * @param uuid   the uuid of backed player
     */
    public RealPlayerData(
        final AdventureLevelPlugin plugin,
        final UUID uuid
    ) {
        markAsIncomplete();

        this.plugin = plugin;
        this.uuid = uuid;
        this.levelMap = new ConcurrentHashMap<>();
    }

    /**
     * You must pass in a complete set of data to this constructor.
     *
     * @param plugin       the plugin instance
     * @param uuid         the uuid of backed player
     * @param levelMap the map must already be filled with instances of all levels
     */
    public RealPlayerData(
        final AdventureLevelPlugin plugin,
        final UUID uuid,
        final ConcurrentHashMap<LevelCategory, Level> levelMap
    ) {
        markAsComplete();

        this.plugin = plugin;
        this.uuid = uuid;
        this.levelMap = levelMap;
    }

    @Override public @NotNull UUID getUuid() {
        return uuid;
    }

    @Override public @NotNull Level getLevel(LevelCategory category) {
        return Objects.requireNonNull(levelMap.get(category));
    }

    @Override public @NotNull Map<LevelCategory, Level> asMap() {
        return levelMap;
    }

    @Override public boolean complete() {
        return complete.get();
    }

    @Override public PlayerData markAsIncomplete() {
        complete.set(false);
        return this;
    }

    @Override public PlayerData markAsComplete() {
        complete.set(true);
        return this;
    }
}
