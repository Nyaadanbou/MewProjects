package cc.mewcraft.mewfishing.loot.impl.condition;

import cc.mewcraft.mewcore.util.RangeUtils;
import cc.mewcraft.mewfishing.event.FishLootEvent;
import cc.mewcraft.mewfishing.loot.api.Conditioned;
import com.google.common.collect.Range;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

@DefaultQualifier(NonNull.class)
public class HeightCondition implements Conditioned {
    private final List<Range<Integer>> heightList;

    public HeightCondition(List<String> heightList) {
        this.heightList = heightList
            .stream()
            .map(RangeUtils::of)
            .toList();
    }

    @Override public boolean evaluate(final FishLootEvent event) {
        if (heightList.isEmpty()) {
            return true;
        }

        int y = event.getFishEvent().getHook().getLocation().getBlockY();
        return heightList.stream().anyMatch(t -> t.contains(y));
    }
}
