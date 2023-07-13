package cc.mewcraft.townybonus.object.bonus;

import cc.mewcraft.townybonus.util.Raffle;
import me.lucko.helper.random.VariableAmount;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class BonusMoney implements Bonus {

    private final String name;
    private final double amountMin;
    private final double amountMax;
    private final double multiplier;
    private final Raffle raffle1;
    private final Raffle raffle2;
    private final EnumSet<NoticeType> noticeOpts;

    public BonusMoney(String name, double amountMin, double amountMax, double multiplier, Raffle raffle1, Raffle raffle2) {
        this.name = name.toLowerCase();
        this.amountMin = amountMin;
        this.amountMax = amountMax;
        this.multiplier = multiplier;
        this.raffle1 = raffle1;
        this.raffle2 = raffle2;
        this.noticeOpts = EnumSet.of(NoticeType.NONE);
    }

    @Override
    public @NotNull BonusType getType() {
        return BonusType.MONEY;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull EnumSet<NoticeType> getNoticeOptions() {
        return noticeOpts;
    }

    /**
     * Represents a one-time random money bonus.
     */
    public static class Roll {
        private final double baseMoney;
        private final double multiplier;
        private final boolean raffle1;
        private final boolean raffle2;

        private Roll(BonusMoney bonus) {
            // random process are done in initialisation stage

            this.raffle1 = bonus.raffle1.test();
            this.raffle2 = bonus.raffle2.test();
            this.baseMoney = VariableAmount.range(bonus.amountMin, bonus.amountMax + 1).getFlooredAmount();
            this.multiplier = bonus.multiplier;
        }

        public static Roll guess(BonusMoney bonusMoney) {
            return new Roll(bonusMoney);
        }

        // read only methods
        // always return the same values
        // no matter how many times these being called

        public double getBaseMoney() {
            return baseMoney;
        }

        public double getJackpotMoney() {
            return baseMoney * multiplier;
        }

        public boolean getRaffle1Outcome() {
            return raffle1;
        }

        public boolean getRaffle2Outcome() {
            return raffle2;
        }

    }

}
