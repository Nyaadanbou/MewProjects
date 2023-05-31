package cc.mewcraft.adventurelevel.data;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import cc.mewcraft.adventurelevel.level.LevelBeanFactory;
import cc.mewcraft.adventurelevel.level.category.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class RealPlayerData implements PlayerData {

    private final AdventureLevelPlugin plugin;
    private final UUID uuid;
    /**
     * A variable indicating whether this player data has been fully loaded. If true (=complete), that means the data
     * has been fully loaded, and getters will return current values; otherwise, false (=incomplete) means it's not been
     * fully loaded and the returned values should not be used.
     */
    private final AtomicBoolean complete = new AtomicBoolean(false);

    // --- level data ---

    private final LevelBean mainLevel;
    private final Map<LevelBean.Category, LevelBean> cateLevelMap;

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

        this.mainLevel = LevelBeanFactory.createMainLevelBean();
        this.cateLevelMap = new HashMap<>();

        this.markAsIncomplete();
    }

    /**
     * You must pass in a complete set of data to this constructor.
     *
     * @param plugin       the plugin instance
     * @param uuid         the uuid of backed player
     * @param mainLevel    the instance of main level
     * @param cateLevelMap the map must already be filled with instances of each categorical level
     */
    public RealPlayerData(
        final AdventureLevelPlugin plugin,
        final UUID uuid,
        final MainLevelBean mainLevel,
        final Map<LevelBean.Category, LevelBean> cateLevelMap
    ) {
        this.plugin = plugin;
        this.uuid = uuid;

        this.mainLevel = mainLevel;
        this.cateLevelMap = cateLevelMap;

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
        this(plugin, uuid, mainLevel, new HashMap<>() {{
            put(LevelBean.Category.BLOCK_BREAK, blockBreakLevel);
            put(LevelBean.Category.BREED, breedLevel);
            put(LevelBean.Category.ENTITY_DEATH, entityDeathLevel);
            put(LevelBean.Category.EXP_BOTTLE, expBottleLevel);
            put(LevelBean.Category.FISHING, fishingLevel);
            put(LevelBean.Category.FURNACE, furnaceLevel);
            put(LevelBean.Category.GRINDSTONE, grindstoneLevel);
            put(LevelBean.Category.PLAYER_DEATH, playerDeathLevel);
            put(LevelBean.Category.VILLAGER_TRADE, villagerTradeLevel);
        }});
    }

    //</editor-fold>

    @Override public @NotNull UUID getUuid() {
        return uuid;
    }

    @Override public @NotNull LevelBean getCateLevel(LevelBean.Category category) {
        return Objects.requireNonNull(cateLevelMap.get(category));
    }

    @Override public @NotNull Map<LevelBean.Category, LevelBean> getCateLevelMap() {
        return cateLevelMap;
    }

    @Override public @NotNull LevelBean getMainLevel() {
        return mainLevel;
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
