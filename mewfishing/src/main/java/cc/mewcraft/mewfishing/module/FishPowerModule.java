package cc.mewcraft.mewfishing.module;

import cc.mewcraft.mewcore.cooldown.ChargeBasedCooldown;
import cc.mewcraft.mewcore.cooldown.ChargeBasedCooldownMap;
import cc.mewcraft.mewcore.progressbar.ProgressbarGenerator;
import cc.mewcraft.mewcore.progressbar.ProgressbarMessenger;
import cc.mewcraft.mewcore.util.UtilTowny;
import cc.mewcraft.mewfishing.MewFishing;
import cc.mewcraft.mewfishing.MewPerms;
import cc.mewcraft.mewfishing.event.AutoFishEvent;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import me.lucko.helper.Events;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataKey;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.api.GemsEconomyAPI;
import me.xanium.gemseconomy.currency.Currency;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class FishPowerModule implements TerminableModule {

    private static final MetadataKey<Cooldown> KEY_HELP_COOLDOWN = MetadataKey.createCooldownKey("msg.tip.cooldown");
    private static final MetadataKey<Cooldown> KEY_WARN_COOLDOWN = MetadataKey.createCooldownKey("msg.warn.cooldown");
    private static final int WARN_INTERVAL_SEC = 1;
    private static final int HELP_INTERVAL_SEC = 300;

    private final ChargeBasedCooldownMap<UUID> fishingPowerMap;
    private final ProgressbarMessenger progressbarMessenger;

    public FishPowerModule() {
        // cache loader to get the maximum fishing charge
        LoadingCache<UUID, Integer> balanceCache = CacheBuilder.newBuilder()
            .expireAfterAccess(MewFishing.conf().baseTimeout(), TimeUnit.SECONDS)
            .build(new CacheLoader<>() {
                @Override
                public @NotNull Integer load(@NotNull UUID key) {
                    GemsEconomyAPI api = GemsEconomy.getAPI();
                    String currencyName = MewFishing.conf().currencyName();
                    Currency currency = api.getCurrency(currencyName);
                    if (currency == null) {
                        MewFishing.instance().getLogger().severe("Currency name not found: " + currencyName);
                        MewFishing.instance().getLogger().severe("Fishing power system will be disabled");
                        MewFishing.conf().setFishingPowerEnabled(false);
                        return 0;
                    }
                    double balance = api.getBalance(key, currency);
                    return (int) balance;
                }
            });

        Cooldown base = Cooldown.of(MewFishing.conf().baseTimeout(), TimeUnit.SECONDS); // base cooldown (i.e., a single charge)
        this.fishingPowerMap = ChargeBasedCooldownMap.create(base, balanceCache::getUnchecked);
        this.progressbarMessenger = new ProgressbarMessenger(
            MewFishing.conf().progressbarStayTime(),
            ProgressbarGenerator.Builder.builder()
                .left(MewFishing.translations().of("cooldownProgressbar.left").plain())
                .full(MewFishing.translations().of("cooldownProgressbar.full").plain())
                .empty(MewFishing.translations().of("cooldownProgressbar.empty").plain())
                .right(MewFishing.translations().of("cooldownProgressbar.right").plain())
                .width(MewFishing.conf().progressbarWidth())
                .build()
        );
    }

    public ChargeBasedCooldownMap<UUID> getFishingPowerMap() {
        return fishingPowerMap;
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        if (!MewFishing.conf().fishingPowerEnabled()) {
            MewFishing.log("FishingPower", false);
            return;
        }

        Events.subscribe(PlayerFishEvent.class, EventPriority.HIGHEST) // highest priority - make sure it's last to run
            .filter(e -> !e.getPlayer().hasPermission(MewPerms.PERM_ADMIN))
            .filter(e -> UtilTowny.isInWilderness(e.getHook().getLocation()))
            .handler(this::onFish)
            .bindWith(consumer);

        // show progress info about charges when toggling sneak
        Events.subscribe(PlayerToggleSneakEvent.class)
            .filter(e -> e.getPlayer().getFishHook() != null && UtilTowny.isInWilderness(e.getPlayer().getLocation()))
            .handler(e -> showProgressbar(e.getPlayer()))
            .bindWith(consumer);

        // cancel auto fishing if charges are run out
        Events.subscribe(AutoFishEvent.class)
            .filter(e -> !fishingPowerMap.get(e.getPlayer().getUniqueId()).testSilently() && UtilTowny.isInWilderness(e.getHook().getLocation()))
            .handler(e -> e.setCancelled(true))
            .bindWith(consumer);
    }

    private void showProgressbar(Player player) {
        ChargeBasedCooldown cooldown = fishingPowerMap.get(player.getUniqueId());
        progressbarMessenger.show(
            player,
            () -> cooldown.elapsedOne() / (float) cooldown.getBaseTimeout(),
            () -> MewFishing.translations().of("cooldownProgressbar.head").plain(),
            () -> MewFishing.translations().of("cooldownProgressbar.tail")
                .replace("remaining", cooldown.remainingTime(TimeUnit.SECONDS))
                .replace("amount", cooldown.getAvailable())
                .plain()
        );
    }

    private void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        showProgressbar(player);

        if (Metadata.provideForPlayer(player).getOrPut(KEY_HELP_COOLDOWN, () -> Cooldown.of(HELP_INTERVAL_SEC, TimeUnit.SECONDS)).test()) {
            MewFishing.translations().of("tipSneakCheckStatus").send(player);
        }

        if (EnumSet.of(State.CAUGHT_FISH, State.REEL_IN, State.BITE).contains(event.getState())) {
            // only count caught

            Entity caught = event.getCaught();
            if (!(caught instanceof Item item))
                return;

            for (String freeItem : MewFishing.conf().freeItems()) {
                if (freeItem.contains(item.getItemStack().getType().name().toLowerCase()))
                    return;
            }

            ChargeBasedCooldown cooldown = fishingPowerMap.get(player.getUniqueId());
            if (!cooldown.test()) { // player has no charge
                showProgressbar(player);
                event.getHook().remove();
                event.setCancelled(true);
                if (Metadata.provideForPlayer(player).getOrPut(KEY_WARN_COOLDOWN, () -> Cooldown.of(WARN_INTERVAL_SEC, TimeUnit.SECONDS)).test()) {
                    MewFishing.translations().of("fishingInWilderness").send(player);
                }
            }
        }
    }

}
