package cc.mewcraft.townybonus.object.bonus;

import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public interface Bonus {

    @NotNull
    BonusType getType();

    @NotNull
    String getName();

    @NotNull
    EnumSet<NoticeType> getNoticeOptions();

}
