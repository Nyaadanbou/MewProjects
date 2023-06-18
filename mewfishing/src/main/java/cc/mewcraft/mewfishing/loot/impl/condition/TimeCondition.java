package cc.mewcraft.mewfishing.loot.impl.condition;

import cc.mewcraft.mewcore.util.RangeUtils;
import cc.mewcraft.mewfishing.event.FishLootEvent;
import cc.mewcraft.mewfishing.loot.api.Conditioned;
import com.google.common.collect.Range;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

@DefaultQualifier(NonNull.class)
public class TimeCondition implements Conditioned {
    private final List<Range<Integer>> timeList;

    public TimeCondition(List<String> timeList) {
        this.timeList = timeList
            .stream()
            .map(RangeUtils::of)
            .toList();
    }

    @Override public boolean evaluate(final FishLootEvent event) {
        if (timeList.isEmpty()) {
            return true;
        }

        int time = (int) event.getPlayer().getWorld().getTime();
        return timeList.stream().anyMatch(t -> t.contains(time));
    }
}
