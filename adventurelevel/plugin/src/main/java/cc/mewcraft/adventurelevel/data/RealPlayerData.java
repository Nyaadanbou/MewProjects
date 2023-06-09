package cc.mewcraft.adventurelevel.data;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import cc.mewcraft.adventurelevel.level.category.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
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
    private final Map<LevelCategory, LevelBean> cateLevelMap;
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
        this.plugin = plugin;
        this.uuid = uuid;
        this.cateLevelMap = new HashMap<>();

        this.markAsIncomplete();
    }

    /**
     * You must pass in a complete set of data to this constructor.
     *
     * @param plugin   the plugin instance
     * @param uuid     the uuid of backed player
     * @param levelMap the map must already be filled with instances of all level bean
     */
    public RealPlayerData(
        final AdventureLevelPlugin plugin,
        final UUID uuid,
        final Map<LevelCategory, LevelBean> levelMap
    ) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.cateLevelMap = levelMap;

        this.markAsComplete();
    }

    //<editor-fold desc="Unused">

    /**
     * You must pass in a complete set of data to this constructor.
     *
     * @param plugin the plugin instance
     * @param uuid   the uuid of backed player
     */
    public RealPlayerData(
        final AdventureLevelPlugin plugin,
        final UUID uuid,
        final MainLevelBean mainLevel,
        final BlockBreakLevelBean blockBreakLevel,
        final BreedLevelBean breedLevel,
        final EntityDeathLevelBean entityDeathLevel,
        final ExpBottleLevelBean expBottleLevel,
        final FishingLevelBean fishingLevel,
        final FurnaceLevelBean furnaceLevel,
        final GrindstoneLevelBean grindstoneLevel,
        final PlayerDeathLevelBean playerDeathLevel,
        final VillagerTradeLevelBean villagerTradeLevel
    ) {
        this(plugin, uuid, new HashMap<>() {{
            put(LevelCategory.MAIN, mainLevel);
            put(LevelCategory.BLOCK_BREAK, blockBreakLevel);
            put(LevelCategory.BREED, breedLevel);
            put(LevelCategory.ENTITY_DEATH, entityDeathLevel);
            put(LevelCategory.EXP_BOTTLE, expBottleLevel);
            put(LevelCategory.FISHING, fishingLevel);
            put(LevelCategory.FURNACE, furnaceLevel);
            put(LevelCategory.GRINDSTONE, grindstoneLevel);
            put(LevelCategory.PLAYER_DEATH, playerDeathLevel);
            put(LevelCategory.VILLAGER_TRADE, villagerTradeLevel);
        }});
    }

    //</editor-fold>

    @Override public @NotNull UUID getUuid() {
        return uuid;
    }

    @Override public @NotNull LevelBean getLevelBean(LevelCategory category) {
        return Objects.requireNonNull(cateLevelMap.get(category));
    }

    @Override public @NotNull Map<LevelCategory, LevelBean> getLevelBeanMap() {
        return cateLevelMap;
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
