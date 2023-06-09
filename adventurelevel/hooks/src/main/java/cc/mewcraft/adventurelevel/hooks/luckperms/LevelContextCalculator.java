package cc.mewcraft.adventurelevel.hooks.luckperms;

import cc.mewcraft.adventurelevel.data.PlayerData;
import cc.mewcraft.adventurelevel.data.PlayerDataManager;
import cc.mewcraft.adventurelevel.level.category.LevelCategory;
import com.google.inject.Inject;
import me.lucko.helper.promise.Promise;
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
        // Implementation Notes:
        // The LuckPerms contexts are requested at early stage of player initialization.
        // Usually the PlayerData is not available upon requesting, so we must return
        // "dummy" values when not available yet.

        LuckPermsProvider.get().getContextManager().registerCalculator((target, consumer) -> {
            Promise<PlayerData> promise = playerDataManager.load((OfflinePlayer) target);
            consumer.accept("adventure-level",
                promise.isDone()
                    ? String.valueOf(promise.join().getLevelBean(LevelCategory.MAIN).getLevel())
                    : "0"
            );
        });
    }
}
