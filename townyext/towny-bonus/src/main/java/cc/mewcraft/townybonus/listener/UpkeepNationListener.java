package cc.mewcraft.townybonus.listener;

import cc.mewcraft.townybonus.object.culture.CultureLevel;
import cc.mewcraft.townybonus.TownyBonus;
import cc.mewcraft.townybonus.object.bonus.BonusUpkeepNation;
import cc.mewcraft.townybonus.util.UtilCulture;
import com.palmergames.bukkit.towny.event.NationUpkeepCalculationEvent;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
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

public class UpkeepNationListener implements TerminableModule {

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {

        // Nation culture bonus
        Events.subscribe(NationUpkeepCalculationEvent.class).handler(e -> {

            final Nation nation = e.getNation();
            final double upkeep = e.getUpkeep();

            final CultureLevel cultureLevel = UtilCulture.cultureLevelOfNullable(nation);
            if (cultureLevel == null) return;

            final Optional<BonusUpkeepNation> upkeepModifier = cultureLevel.getBonusList().stream()
                    .filter(b -> b instanceof BonusUpkeepNation)
                    .map(b -> (BonusUpkeepNation) b)
                    .findFirst();
            upkeepModifier.ifPresent(b -> {
                final double modified = b.modify(upkeep);
                e.setUpkeep(modified);

                TownyBonus.debug("Nation upkeep modified: %s -> %s".formatted(upkeep, modified));
            });

        }).bindWith(consumer);

        // VIP bonus
        Events.subscribe(NationUpkeepCalculationEvent.class).handler(e -> {
            final Nation nation = e.getNation();
            final double upkeep = e.getUpkeep();

            // Fix NPE from running /towny prices
            if (nation == null) return;

            final LuckPerms lp = LuckPermsProvider.get();
            final AtomicInteger count = new AtomicInteger();
            for (Resident resident : nation.getResidents()) {
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

            final int max = TownyBonus.config().getMituanNationUpkeepMax();
            final int base = TownyBonus.config().getMituanNationUpkeepBase();
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
