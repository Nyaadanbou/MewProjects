package cc.mewcraft.adventurelevel.data;

import cc.mewcraft.adventurelevel.level.category.LevelBean;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PlayerData {
    /**
     * This DUMMY instance is used in the cases where exceptions have occurred upon fetching the data.
     */
    DummyPlayerData DUMMY = new DummyPlayerData();

    @NotNull UUID getUuid();

    @NotNull LevelBean getCateLevel(LevelBean.Category category);

    @NotNull LevelBean getMainLevel();
}
