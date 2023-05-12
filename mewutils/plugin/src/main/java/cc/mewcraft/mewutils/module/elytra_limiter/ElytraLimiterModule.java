package cc.mewcraft.mewutils.module.elytra_limiter;

import cc.mewcraft.mewcore.cooldown.ChargeBasedCooldownMap;
import cc.mewcraft.mewcore.progressbar.ProgressbarGenerator;
import cc.mewcraft.mewcore.progressbar.ProgressbarMessenger;
import cc.mewcraft.mewutils.api.MewPlugin;
import cc.mewcraft.mewutils.api.module.ModuleBase;
import com.google.inject.Inject;
import me.lucko.helper.cooldown.Cooldown;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@DefaultQualifier(NonNull.class)
public class ElytraLimiterModule extends ModuleBase {

    private @MonotonicNonNull Set<String> restrictedWorlds;
    private @MonotonicNonNull Set<BoostMethod> restrictedBoost;
    private @MonotonicNonNull ProgressbarMessenger progressbarMessenger;
    private @MonotonicNonNull ChargeBasedCooldownMap<UUID> cooldownMap;
    private double velocityMultiply;
    private double tpsThreshold;

    @Inject
    public ElytraLimiterModule(MewPlugin plugin) {
        super(plugin);
    }

    @Override protected void load() throws Exception {
        this.restrictedWorlds = new HashSet<>(getConfigNode().node("worlds").getList(String.class, List.of()));

        this.restrictedBoost = getConfigNode().node("methods")
            .getList(String.class, List.of())
            .stream().map(BoostMethod::valueOf)
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(BoostMethod.class)));

        this.cooldownMap = ChargeBasedCooldownMap.create(
            Cooldown.of(getConfigNode().node("cooldown").getInt(), TimeUnit.MILLISECONDS),
            uuid -> getConfigNode().node("charge").getInt()
        );

        this.progressbarMessenger = new ProgressbarMessenger(
            getConfigNode().node("bar_stay_time").getInt(),
            ProgressbarGenerator.Builder.builder()
                .left(getLang().of("slow_elytra.cooldown_progressbar.left").plain())
                .full(getLang().of("slow_elytra.cooldown_progressbar.full").plain())
                .empty(getLang().of("slow_elytra.cooldown_progressbar.empty").plain())
                .right(getLang().of("slow_elytra.cooldown_progressbar.right").plain())
                .width(getConfigNode().node("bar_width").getInt())
                .build()
        );

        this.velocityMultiply = getConfigNode().node("velocity_multiply").getDouble();

        this.tpsThreshold = getConfigNode().node("tps_threshold").getDouble();
    }

    @Override protected void enable() {
        registerListener(new ElytraBoostListener(this));
    }

    public ProgressbarMessenger getProgressbarMessenger() {
        return this.progressbarMessenger;
    }

    public ChargeBasedCooldownMap<UUID> getCooldownMap() {
        return this.cooldownMap;
    }

    public boolean isBoostAllowed(BoostMethod method) {
        return !this.restrictedBoost.contains(method);
    }

    public boolean inRestrictedWorld(Player player) {
        return this.restrictedWorlds.contains(player.getWorld().getName());
    }

    public boolean underTPSThreshold() {
        return getParentPlugin().getServer().getTPS()[0] <= this.tpsThreshold;
    }

    public double getVelocityMultiply() {
        return this.velocityMultiply;
    }

    @Override
    public boolean checkRequirement() {
        return true;
    }
}
