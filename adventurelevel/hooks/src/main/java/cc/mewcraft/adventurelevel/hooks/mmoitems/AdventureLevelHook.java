package cc.mewcraft.adventurelevel.hooks.mmoitems;

import cc.mewcraft.adventurelevel.AdventureLevelProvider;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.comp.rpg.RPGHandler;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;

public class AdventureLevelHook implements RPGHandler {

    @Override public RPGPlayer getInfo(final PlayerData data) {
        Player player = data.getPlayer();
        try {
            return AdventureLevelProvider.get()
                .getPlayerDataManager()
                .load(player)
                .thenApplyAsync(playerData -> new AdventureLevelPlayer(data, playerData))
                .get(); // 50 ms = 1 tick
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException("Fatal error");
        }
    }

    @Override public void refreshStats(final PlayerData data) {
        // no stats to refresh
    }

}