package cc.mewcraft.mewfishing.loot.impl.condition;

import cc.mewcraft.mewfishing.event.FishLootEvent;
import cc.mewcraft.mewfishing.loot.api.Conditioned;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@DefaultQualifier(NonNull.class)
public class WeatherCondition implements Conditioned {
    private final EnumSet<Weather> weathers;

    public WeatherCondition(final List<String> weathers) {
        this.weathers = weathers
            .stream()
            .map(t -> Weather.valueOf(t.toUpperCase()))
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(Weather.class)));
    }

    @Override public boolean evaluate(final FishLootEvent event) {
        if (weathers.isEmpty()) {
            return true;
        }

        World world = event.getFishEvent().getHook().getLocation().getWorld();
        return weathers.stream().allMatch(t -> switch (t) {
            case CLEAR -> world.isClearWeather();
            case NOT_CLEAR -> !world.isClearWeather();
            case THUNDERING -> world.isThundering();
            case NOT_THUNDERING -> !world.isThundering();
        });
    }

    enum Weather {
        CLEAR,
        NOT_CLEAR,
        THUNDERING,
        NOT_THUNDERING
    }
}
