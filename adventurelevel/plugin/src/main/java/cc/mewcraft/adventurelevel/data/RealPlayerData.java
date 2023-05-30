package cc.mewcraft.adventurelevel.data;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import cc.mewcraft.adventurelevel.level.category.*;
import cc.mewcraft.adventurelevel.message.RealPlayerDataCodec;
import me.lucko.helper.messaging.codec.Message;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Message(codec = RealPlayerDataCodec.class) // use specific codec instead of GsonCodec to encode/decode this class
public class RealPlayerData implements PlayerData {
    private final AdventureLevelPlugin plugin;
    private final UUID uuid;
    private final LevelBean mainLevel;
    private final Map<LevelBean.Category, LevelBean> cateLevelMap;

    /**
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
    }

    /**
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

    @Override public @NotNull UUID getUuid() {
        return uuid;
    }

    @Override public @NotNull LevelBean getCateLevel(LevelBean.Category category) {
        return Objects.requireNonNull(cateLevelMap.get(category));
    }

    @Override public @NotNull LevelBean getMainLevel() {
        return mainLevel;
    }

    @Override public void updateWith(final @NotNull PlayerData playerData) {
        // Update experience of Main Level
        mainLevel.setExperience(playerData.getMainLevel().getExperience());

        // Update experience of Categorical Level
        for (final LevelBean.Category cat : cateLevelMap.keySet()) {
            cateLevelMap.replace(cat, playerData.getCateLevel(cat));
        }
    }
}
