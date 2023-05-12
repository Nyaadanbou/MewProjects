package cc.mewcraft.mewfishing.loot.impl.serializer;

import cc.mewcraft.mewcore.item.api.PluginItem;
import cc.mewcraft.mewcore.item.api.PluginItemRegistry;
import cc.mewcraft.mewfishing.loot.api.Conditioned;
import cc.mewcraft.mewfishing.loot.impl.loot.PluginItemLoot;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PluginItemLootSerializer implements TypeSerializer<PluginItemLoot> {
    @Override public PluginItemLoot deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        double weight = node.node("weight").getDouble(1D);
        String amount = node.node("amount").getString("1");
        List<Conditioned> conditions = node.node("conditions").get(new TypeToken<>() {}, Collections.emptyList());
        String id = Objects.requireNonNull(node.node("id").getString(), node.path() + ": id is null");
        PluginItem<?> pluginItem = PluginItemRegistry.get().fromReference(id);
        return new PluginItemLoot(weight, amount, conditions, pluginItem);
    }

    @Override public void serialize(final Type type, @Nullable final PluginItemLoot obj, final ConfigurationNode node) {

    }
}
