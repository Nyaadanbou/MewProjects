package cc.mewcraft.adventurelevel.placeholder;

import cc.mewcraft.adventurelevel.data.PlayerDataManager;
import com.google.inject.Inject;

public class PAPIPlaceholderExpansion {
    private final PlayerDataManager playerDataManager;

    @Inject
    public PAPIPlaceholderExpansion(final PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    public void register() {
        // TODO implement PlaceholderAPI expansion
    }
}
