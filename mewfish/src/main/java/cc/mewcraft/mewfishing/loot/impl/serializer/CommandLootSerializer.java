package cc.mewcraft.mewfishing.loot.impl.serializer;

import cc.mewcraft.mewfishing.loot.api.Conditioned;
import cc.mewcraft.mewfishing.loot.impl.loot.CommandLoot;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CommandLootSerializer implements TypeSerializer<CommandLoot> {
    @Override public CommandLoot deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        double weight = node.node("weight").getDouble(1D);
        String amount = node.node("amount").getString("1");
        List<Conditioned> conditions = node.node("conditions").get(new TypeToken<>() {}, Collections.emptyList());
        List<String> exec = Objects.requireNonNull(node.node("exec").getList(String.class), node.path() + ": exec is null");
        return new CommandLoot(weight, amount, conditions, exec);
    }

    @Override public void serialize(final Type type, @Nullable final CommandLoot obj, final ConfigurationNode node) {

    }
}
