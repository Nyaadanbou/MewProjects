package cc.mewcraft.mewfishing.loot.impl.serializer;

import cc.mewcraft.mewfishing.loot.api.Conditioned;
import cc.mewcraft.mewfishing.loot.impl.condition.BiomeCondition;
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

        // --- biome ---
        List<String> biomeList = node.node("biomes").getList(String.class, ArrayList::new);
        BiomeCondition biomeCondition = new BiomeCondition(Sets.newHashSet(biomeList));
        conditions.add(biomeCondition);

        // --- time ---
        // todo

        // --- height ---
        // todo

        // --- weather ---
        // todo

        // --- moon phase ---
        // todo

        return conditions;
    }

    @Override public void serialize(final Type type, @Nullable final List<Conditioned> obj, final ConfigurationNode node) {

    }
}
