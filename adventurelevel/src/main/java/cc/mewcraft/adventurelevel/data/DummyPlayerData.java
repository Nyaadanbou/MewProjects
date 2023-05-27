package cc.mewcraft.adventurelevel.data;

import cc.mewcraft.adventurelevel.level.category.LevelBean;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DummyPlayerData implements PlayerData {

    private static final UUID DUMMY_UUID = new UUID(0, 0);

    @Override public @NotNull UUID getUuid() {
        return DUMMY_UUID;
    }

    @Override public @NotNull LevelBean getCateLevel(final LevelBean.Category category) {
        return LevelBean.DUMMY;
    }

    @Override public @NotNull LevelBean getMainLevel() {
        return LevelBean.DUMMY;
    }
}
