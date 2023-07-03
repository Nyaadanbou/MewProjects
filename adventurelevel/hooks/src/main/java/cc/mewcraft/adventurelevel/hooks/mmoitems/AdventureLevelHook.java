package cc.mewcraft.adventurelevel.hooks.mmoitems;

import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.comp.rpg.RPGHandler;

public class AdventureLevelHook implements RPGHandler {
    @Override public RPGPlayer getInfo(final PlayerData data) {
        return new AdventureLevelPlayer(data);
    }

    @Override public void refreshStats(final PlayerData data) {
        // no stats to refresh
    }
}