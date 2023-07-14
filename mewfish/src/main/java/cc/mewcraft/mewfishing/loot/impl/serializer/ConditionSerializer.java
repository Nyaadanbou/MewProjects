package cc.mewcraft.mewfishing.loot.impl.serializer;

import cc.mewcraft.mewfishing.loot.api.Conditioned;
import cc.mewcraft.mewfishing.loot.impl.condition.*;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ConditionSerializer implements TypeSerializer<List<Conditioned>> {
    @Override public List<Conditioned> deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        List<Conditioned> conditions = new ArrayList<>();

        // Biome conditions
        List<String> biomeList = node.node("biome").getList(String.class, ArrayList::new);
        BiomeCondition biomeCondition = new BiomeCondition(Sets.newHashSet(biomeList));
        conditions.add(biomeCondition);

        // Cron conditions
        List<String> cronList = node.node("cron_expression").getList(String.class, ArrayList::new);
        CronCondition cronCondition = new CronCondition(cronList);
        conditions.add(cronCondition);

        // Server conditions
        List<String> serverList = node.node("server").getList(String.class, ArrayList::new);
        ServerCondition serverCondition = new ServerCondition(serverList);
        conditions.add(serverCondition);

        // Weather conditions
        List<String> weatherList = node.node("weather").getList(String.class, ArrayList::new);
        WeatherCondition weatherCondition = new WeatherCondition(weatherList);
        conditions.add(weatherCondition);

        // Moon phase conditions
        List<String> moonPhaseList = node.node("moon_phase").getList(String.class, ArrayList::new);
        MoonPhaseCondition moonPhaseCondition = new MoonPhaseCondition(moonPhaseList);
        conditions.add(moonPhaseCondition);

        // Height conditions
        List<String> heightList = node.node("height").getList(String.class, ArrayList::new);
        HeightCondition heightCondition = new HeightCondition(heightList);
        conditions.add(heightCondition);

        // Time conditions
        List<String> timeList = node.node("time").getList(String.class, ArrayList::new);
        TimeCondition timeCondition = new TimeCondition(timeList);
        conditions.add(timeCondition);

        return conditions;
    }

    @Override public void serialize(final Type type, final @Nullable List<Conditioned> obj, final ConfigurationNode node) {

    }
}
