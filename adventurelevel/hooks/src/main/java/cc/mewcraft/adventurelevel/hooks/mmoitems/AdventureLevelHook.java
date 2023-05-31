package cc.mewcraft.adventurelevel.hooks.mmoitems;

import cc.mewcraft.adventurelevel.AdventureLevelProvider;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.comp.rpg.RPGHandler;

public class AdventureLevelHook implements RPGHandler {

    @Override public RPGPlayer getInfo(final PlayerData data) {
        AdventureLevelPlayer rpgPlayer = new AdventureLevelPlayer(data);

        // Set the backed AdventurePlayerData instance using an async callback
        AdventureLevelProvider.get()
            .getPlayerDataManager()
            .load(data.getPlayer())
            .thenAcceptAsync(rpgPlayer::setAdventurePlayerData);

        return rpgPlayer;
    }

    @Override public void refreshStats(final PlayerData data) {
        // no stats to refresh
    }

}