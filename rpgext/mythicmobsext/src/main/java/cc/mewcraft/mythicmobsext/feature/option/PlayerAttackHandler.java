package cc.mewcraft.mythicmobsext.feature.option;

import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import org.jetbrains.annotations.NotNull;

public interface PlayerAttackHandler {

    void handle(@NotNull PlayerAttackEvent event, @NotNull ActiveMob activeMob);

}
