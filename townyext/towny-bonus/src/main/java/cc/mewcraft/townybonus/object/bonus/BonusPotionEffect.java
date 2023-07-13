package cc.mewcraft.townybonus.object.bonus;

import cc.mewcraft.townybonus.util.Raffle;
import me.lucko.helper.random.RandomSelector;
import me.lucko.helper.random.VariableAmount;
import me.lucko.helper.random.WeightedObject;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class BonusPotionEffect implements Bonus {

    private final String name;
    private final Raffle raffle;
    private final int minAmount;
    private final int maxAmount;
    private final List<WeightedObject<PotionEffect>> effectPool;
    private final EnumSet<NoticeType> noticeOpts;

    public BonusPotionEffect(String name, Raffle raffle, int minAmount, int maxAmount) {
        this.name = name.toLowerCase();
        this.raffle = raffle;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.effectPool = new ArrayList<>();
        this.noticeOpts = EnumSet.of(NoticeType.NONE);
    }

    @Override
    public @NotNull BonusType getType() {
        return BonusType.POTION_EFFECT;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull EnumSet<NoticeType> getNoticeOptions() {
        return noticeOpts;
    }

    public void addEffect(PotionEffectType type, int duration, int amplifier, double weight) {
        effectPool.add(WeightedObject.of(new PotionEffect(type, duration, amplifier), weight));
    }

    public void removeEffect(PotionEffectType type) {
        effectPool.removeIf(e -> e.get().getType() == type);
    }

    public List<PotionEffect> roll() {
        if (!raffle.test()) {
            return List.of();
        }
        int amount = VariableAmount.range(minAmount, maxAmount + 1).getFlooredAmount();
        return RandomSelector.weighted(effectPool).stream().limit(amount).map(WeightedObject::get).toList();
    }

}
