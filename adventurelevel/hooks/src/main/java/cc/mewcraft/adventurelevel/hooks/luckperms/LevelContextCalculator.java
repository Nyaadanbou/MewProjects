package cc.mewcraft.adventurelevel.hooks.luckperms;

import cc.mewcraft.adventurelevel.data.PlayerData;
import cc.mewcraft.adventurelevel.data.PlayerDataManager;
import cc.mewcraft.adventurelevel.level.category.LevelCategory;
import com.google.inject.Inject;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.OfflinePlayer;
import org.checkerframework.checker.nullness.qual.NonNull;

public class LevelContextCalculator {
    private final PlayerDataManager playerDataManager;

    @Inject
    public LevelContextCalculator(final @NonNull PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    public void register() {
        LuckPermsProvider.get().getContextManager().registerCalculator((target, consumer) -> {
            PlayerData playerData = playerDataManager.load((OfflinePlayer) target).join();
            String mainLevel = String.valueOf(playerData.getLevelBean(LevelCategory.MAIN).getLevel());
            consumer.accept("adventure-level", mainLevel);
        });
    }
}
