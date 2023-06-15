package cc.mewcraft.townybonus.object.bonus;

import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class BonusUpkeepNation implements Bonus {

    private final String name;
    private final double multiplier;
    private final EnumSet<NoticeType> noticeOpts;

    public BonusUpkeepNation(String name, double multiplier) {
        this.name = name.toLowerCase();
        this.multiplier = multiplier;
        this.noticeOpts = EnumSet.of(NoticeType.NONE);
    }

    @Override
    public @NotNull BonusType getType() {
        return BonusType.NATION_UPKEEP_MODIFIER;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull EnumSet<NoticeType> getNoticeOptions() {
        return noticeOpts;
    }

    public double modify(double upkeep) {
        return upkeep * (1 + multiplier);
    }

}
