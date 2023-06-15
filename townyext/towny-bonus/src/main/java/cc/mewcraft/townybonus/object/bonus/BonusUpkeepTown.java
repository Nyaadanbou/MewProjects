package cc.mewcraft.townybonus.object.bonus;

import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class BonusUpkeepTown implements Bonus {

    private final String name;
    private final double multiplier;
    private final EnumSet<NoticeType> noticeOpts;

    public BonusUpkeepTown(String name, double multiplier) {
        this.name = name.toLowerCase();
        this.multiplier = multiplier;
        this.noticeOpts = EnumSet.of(NoticeType.NONE);
    }

    @Override
    public @NotNull BonusType getType() {
        return BonusType.TOWN_UPKEEP_MODIFIER;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BonusUpkeepTown that = (BonusUpkeepTown) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
