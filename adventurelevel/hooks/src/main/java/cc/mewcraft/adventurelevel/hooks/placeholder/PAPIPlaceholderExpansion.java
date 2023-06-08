package cc.mewcraft.adventurelevel.hooks.placeholder;

import cc.mewcraft.adventurelevel.data.PlayerDataManager;
import com.google.inject.Inject;
import me.lucko.helper.terminable.Terminable;

public class PAPIPlaceholderExpansion implements Terminable {
    private final PlayerDataManager playerDataManager;

    @Inject
    public PAPIPlaceholderExpansion(final PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    public PAPIPlaceholderExpansion register() {

        return this;
    }

    @Override public void close() {

    }
}
