package cc.mewcraft.mewfishing.module.fishpower;

import cc.mewcraft.mewcore.cooldown.StackableCooldown;
import cc.mewcraft.mewcore.cooldown.StackableCooldownMap;
import cc.mewcraft.mewfishing.MewFishing;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import me.lucko.helper.cooldown.Cooldown;
import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.api.GemsEconomyAPI;
import me.xanium.gemseconomy.currency.Currency;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * This class provides cooldown lookup and creation.
 */
public class CooldownManager {

    /**
     * Stores fishing power cooldown of all players.
     */
    private final StackableCooldownMap<UUID> cooldownMap;

    CooldownManager(final MewFishing plugin) {
        LoadingCache<UUID, Long> powerCache = CacheBuilder.newBuilder() // Used to get the maximum stacks of cooldown
            .expireAfterWrite(Duration.of(plugin.config().powerTimeout(), ChronoUnit.SECONDS))
            .build(CacheLoader.from(k -> {
                GemsEconomyAPI api = GemsEconomy.getAPI();
                String currencyName = plugin.config().currencyName();
                Currency currency = api.getCurrency(currencyName);
                if (currency == null) {
                    plugin.log(Level.SEVERE, "Currency not found: " + currencyName);
                    return 0L;
                }
                return (long) api.getBalance(k, currency);
            }));
        Cooldown base = Cooldown.of(plugin.config().powerTimeout(), TimeUnit.SECONDS);
        this.cooldownMap = StackableCooldownMap.create(base, powerCache::getUnchecked);
    }

    public StackableCooldown getData(UUID uuid) {
        return cooldownMap.get(uuid);
    }

}
