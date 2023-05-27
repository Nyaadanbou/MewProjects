package cc.mewcraft.adventurelevel;

import cc.mewcraft.adventurelevel.data.PlayerDataManager;
import org.jetbrains.annotations.NotNull;

public interface AdventureLevel {

    @NotNull PlayerDataManager getPlayerDataManager();

}
