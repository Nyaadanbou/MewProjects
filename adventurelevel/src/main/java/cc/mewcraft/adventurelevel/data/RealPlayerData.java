package cc.mewcraft.adventurelevel.data;

import cc.mewcraft.adventurelevel.AdventureLevel;
import cc.mewcraft.adventurelevel.level.category.LevelBean;
import cc.mewcraft.adventurelevel.level.category.MainLevelBean;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class RealPlayerData implements PlayerData {
    private final AdventureLevel plugin;
    private final UUID uuid;
    private final LevelBean mainLevel;
    private final Map<LevelBean.Category, LevelBean> subLevelMap;

    /**
     * @param plugin      the plugin instance
     * @param uuid        the uuid of backed player
     * @param mainLevel   the Main Level instance
     * @param subLevelMap the map must already be fully filled with usable data
     */
    public RealPlayerData(
        final AdventureLevel plugin,
        final UUID uuid,
        final MainLevelBean mainLevel,
        final Map<LevelBean.Category, LevelBean> subLevelMap
    ) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.mainLevel = mainLevel;
        this.subLevelMap = subLevelMap;
    }

    @Override public @NotNull UUID getUuid() {
        return uuid;
    }

    @Override public @NotNull LevelBean getCateLevel(LevelBean.Category category) {
        return Objects.requireNonNull(subLevelMap.get(category));
    }

    @Override public @NotNull LevelBean getMainLevel() {
        return mainLevel;
    }
}
