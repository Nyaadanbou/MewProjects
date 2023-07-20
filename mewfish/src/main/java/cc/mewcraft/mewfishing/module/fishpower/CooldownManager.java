package cc.mewcraft.mewfishing.module.fishpower;

import cc.mewcraft.mewcore.cooldown.StackableCooldown;
import cc.mewcraft.mewcore.cooldown.StackableCooldownMap;
import cc.mewcraft.mewfishing.MewFish;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.lucko.helper.cooldown.Cooldown;
import me.xanium.gemseconomy.api.Currency;
import me.xanium.gemseconomy.api.GemsEconomy;
import me.xanium.gemseconomy.api.GemsEconomyProvider;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * This class provides cooldown lookup and creation.
 */
@Singleton
public class CooldownManager {

    /**
     * Stores fishing power cooldown of all players.
     */
    private final StackableCooldownMap<UUID> cooldownMap;

    @Inject CooldownManager(final MewFish plugin) {
        LoadingCache<UUID, Long> powerCache = CacheBuilder.newBuilder() // Used to get the maximum stacks of cooldown
            .expireAfterWrite(Duration.of(plugin.config().powerTimeout(), ChronoUnit.SECONDS))
            .build(CacheLoader.from(k -> {
                GemsEconomy economy = GemsEconomyProvider.get();
                String currencyName = plugin.config().currencyName();
                Currency currency = economy.getCurrency(currencyName);
                if (currency == null) {
                    plugin.log(Level.SEVERE, "Currency not found: " + currencyName);
                    return 0L;
                }
                return (long) economy.getBalance(k, currency);
            }));
        Cooldown base = Cooldown.of(plugin.config().powerTimeout(), TimeUnit.SECONDS);
        this.cooldownMap = StackableCooldownMap.create(base, powerCache::getUnchecked);
    }

    public StackableCooldown getData(UUID uuid) {
        return cooldownMap.get(uuid);
    }

}
