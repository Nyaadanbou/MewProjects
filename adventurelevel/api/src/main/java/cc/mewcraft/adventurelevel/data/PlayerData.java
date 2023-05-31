package cc.mewcraft.adventurelevel.data;

import cc.mewcraft.adventurelevel.level.category.LevelBean;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("UnusedReturnValue")
public interface PlayerData {

    /**
     * This DUMMY instance is only used in the cases where:
     * <ol>
     *     <li>requested data does not exist in the datasource</li>
     *     <li>exceptions have occurred upon fetching the data</li>
     * </ol>
     */
    DummyPlayerData DUMMY = new DummyPlayerData();

    @NotNull UUID getUuid();

    @NotNull LevelBean getCateLevel(LevelBean.Category category);

    @NotNull Map<LevelBean.Category, LevelBean> getCateLevelMap();

    @NotNull LevelBean getMainLevel();

    /**
     * Checks whether this PlayerData has been fully loaded, i.e., its states are valid and up-to-date.
     *
     * @return true if this PlayerData has been fully loaded (states are up-to-date); otherwise false
     */
    boolean complete();

    /**
     * Marks this PlayerData as not fully loaded.
     *
     * @return this object
     */
    PlayerData markAsIncomplete();

    /**
     * Marks this PlayerData as fully loaded.
     *
     * @return this object
     */
    PlayerData markAsComplete();
}
