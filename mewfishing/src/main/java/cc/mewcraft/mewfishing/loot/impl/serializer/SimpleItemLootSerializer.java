package cc.mewcraft.mewfishing.loot.impl.serializer;

import cc.mewcraft.mewfishing.loot.api.Conditioned;
import cc.mewcraft.mewfishing.loot.impl.loot.SimpleItemLoot;
import io.leangen.geantyref.TypeToken;
import org.bukkit.Material;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SimpleItemLootSerializer implements TypeSerializer<SimpleItemLoot> {
    @Override public SimpleItemLoot deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        double weight = node.node("weight").getDouble(1D);
        String amount = node.node("amount").getString("1");
        List<Conditioned> conditions = node.node("conditions").get(new TypeToken<>() {}, Collections.emptyList());
        String id = Objects.requireNonNull(node.node("id").getString(), node.path() + ": id is null");
        Material material = Objects.requireNonNull(Material.matchMaterial(id), node.path() + ": material is null");
        return new SimpleItemLoot(weight, amount, conditions, material);
    }

    @Override public void serialize(final Type type, @Nullable final SimpleItemLoot obj, final ConfigurationNode node) {

    }
}
