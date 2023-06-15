package cc.mewcraft.townybonus.object.bonus;

import cc.mewcraft.townybonus.util.Raffle;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class BonusExp implements Bonus {

    private final String name;
    private final Raffle raffle;
    private final double multiplier;
    private final EnumSet<NoticeType> noticeOpts;

    public BonusExp(String name, Raffle raffle, double multiplier) {
        this.name = name.toLowerCase();
        this.raffle = raffle;
        this.multiplier = multiplier;
        this.noticeOpts = EnumSet.of(NoticeType.NONE);
    }

    @Override
    public @NotNull BonusType getType() {
        return BonusType.EXP;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull EnumSet<NoticeType> getNoticeOptions() {
        return noticeOpts;
    }

    public int modify(int expAmount) {
        if (!raffle.test()) {
            // unlucky, return what it is
            return expAmount;
        }
        return (int) Math.floor(expAmount * multiplier);
    }

}
