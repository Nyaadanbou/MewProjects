package cc.mewcraft.mewfishing.loot.impl.serializer;

import cc.mewcraft.mewfishing.loot.api.Conditioned;
import cc.mewcraft.mewfishing.loot.impl.loot.TableLoot;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TableLootSerializer implements TypeSerializer<TableLoot> {
    @Override public TableLoot deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        double weight = node.node("weight").getDouble(1D);
        String amount = node.node("amount").getString("1");
        List<Conditioned> conditions = node.node("conditions").get(new TypeToken<>() {}, Collections.emptyList());
        String id = Objects.requireNonNull(node.node("id").getString(), node.path() + ": id is null");
        return new TableLoot(weight, amount, conditions, id);
    }

    @Override public void serialize(final Type type, @Nullable final TableLoot obj, final ConfigurationNode node) {

    }
}
