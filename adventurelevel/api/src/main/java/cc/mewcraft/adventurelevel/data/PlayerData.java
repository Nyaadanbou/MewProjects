package cc.mewcraft.adventurelevel.data;

import cc.mewcraft.adventurelevel.level.category.LevelBean;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PlayerData {
    /**
     * This DUMMY instance is used in the cases where:
     * <ol>
     *     <li>requested data does not exist in the datasource</li>
     *     <li>exceptions have occurred upon fetching the data</li>
     * </ol>
     */
    DummyPlayerData DUMMY = new DummyPlayerData();

    @NotNull UUID getUuid();

    @NotNull LevelBean getCateLevel(LevelBean.Category category);

    @NotNull LevelBean getMainLevel();

    /**
     * Updates data of this instance with the given one.
     *
     * @param playerData the player data to copy from
     */
    void updateWith(@NotNull PlayerData playerData);
}
