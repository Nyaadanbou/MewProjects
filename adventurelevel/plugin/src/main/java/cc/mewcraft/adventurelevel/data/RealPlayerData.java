package cc.mewcraft.adventurelevel.data;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import cc.mewcraft.adventurelevel.level.category.LevelBean;
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
     * A map containing all level beans.
     */
    private final ConcurrentHashMap<LevelCategory, LevelBean> levelBeanMap;
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
        this.levelBeanMap = new ConcurrentHashMap<>();
    }

    /**
     * You must pass in a complete set of data to this constructor.
     *
     * @param plugin       the plugin instance
     * @param uuid         the uuid of backed player
     * @param levelBeanMap the map must already be filled with instances of all level bean
     */
    public RealPlayerData(
        final AdventureLevelPlugin plugin,
        final UUID uuid,
        final ConcurrentHashMap<LevelCategory, LevelBean> levelBeanMap
    ) {
        markAsComplete();

        this.plugin = plugin;
        this.uuid = uuid;
        this.levelBeanMap = levelBeanMap;
    }

    @Override public @NotNull UUID getUuid() {
        return uuid;
    }

    @Override public @NotNull LevelBean getLevelBean(LevelCategory category) {
        return Objects.requireNonNull(levelBeanMap.get(category));
    }

    @Override public @NotNull Map<LevelCategory, LevelBean> getLevelBeanMap() {
        return levelBeanMap;
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
