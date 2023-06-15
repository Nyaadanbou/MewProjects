package cc.mewcraft.townybonus.listener;

import cc.mewcraft.townybonus.object.culture.CultureLevel;
import cc.mewcraft.townybonus.TownyBonus;
import cc.mewcraft.townybonus.object.bonus.BonusUpkeepTown;
import cc.mewcraft.townybonus.util.UtilCulture;
import com.palmergames.bukkit.towny.event.TownUpkeepCalculationEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.model.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class UpkeepTownListener implements TerminableModule {

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {

        /*
         * These upkeep multipliers stack up *multiplicative*
         * */

        // Town bonus under nation culture
        Events.subscribe(TownUpkeepCalculationEvent.class).handler(e -> {

            final Town town = e.getTown();
            final double upkeep = e.getUpkeep();

            final CultureLevel cultureLevel = UtilCulture.cultureLevelOfNullable(town);
            if (cultureLevel == null) return;

            final Optional<BonusUpkeepTown> upkeepModifier = cultureLevel.getBonusList().stream()
                    .filter(b -> b instanceof BonusUpkeepTown)
                    .map(b -> (BonusUpkeepTown) b)
                    .findFirst();
            upkeepModifier.ifPresent(b -> {
                final double modified = b.modify(upkeep);
                e.setUpkeep(modified);

                TownyBonus.debug("Town upkeep modified: %s -> %s".formatted(upkeep, modified));
            });

        }).bindWith(consumer);

        // VIP bonus
        Events.subscribe(TownUpkeepCalculationEvent.class).handler(e -> {
            final Town town = e.getTown();
            final double upkeep = e.getUpkeep();

            // Fix NPE from running /towny prices
            if (town == null) return;

            final LuckPerms lp = LuckPermsProvider.get();
            final AtomicInteger count = new AtomicInteger();
            for (Resident resident : town.getResidents()) {
                if (e.isAsynchronous()) {
                    lp.getUserManager().loadUser(resident.getUUID()).thenAccept(user -> incrementCount(count, user)).join();
                } else {
                    User user = lp.getUserManager().getUser(resident.getUUID());
                    if (user != null) {
                        incrementCount(count, user);
                    } else {
                        lp.getUserManager().loadUser(resident.getUUID());
                    }
                }
            }

            final int max = TownyBonus.config().getMituanTownUpkeepMax();
            final int base = TownyBonus.config().getMituanTownUpkeepBase();
            final double newUpkeep = upkeep * Math.max((100 - max) / 100D, Math.pow((100 - base) / 100D, count.get()));
            e.setUpkeep(newUpkeep);
        }).bindWith(consumer);
    }

    private void incrementCount(AtomicInteger count, User user) {
        final CachedPermissionData permissionData = user.getCachedData().getPermissionData();
        if (permissionData.checkPermission(TownyBonus.config().getMituanGroupName()).asBoolean()) {
            count.incrementAndGet();
        }
    }

}
