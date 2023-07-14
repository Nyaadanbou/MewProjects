package cc.mewcraft.mewfishing.loot.impl.serializer;

import cc.mewcraft.mewfishing.loot.api.Loot;
import cc.mewcraft.mewfishing.loot.impl.loot.*;
import cc.mewcraft.mewfishing.util.RandomCollection;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class LootCollectionSerializer implements TypeSerializer<RandomCollection<Loot>> {
    @Override public RandomCollection<Loot> deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        if (!node.isMap())
            throw new SerializationException(node.path() + ": not a map");

        RandomCollection<Loot> collection = new RandomCollection<>();
        Map<Object, ? extends ConfigurationNode> objectMap = node.childrenMap();
        for (final ConfigurationNode entry : objectMap.values()) {
            String lootType = Objects.requireNonNull(entry.node("type").getString(), node.path() + ": type is null");
            switch (lootType.toLowerCase(Locale.ROOT)) {
                case "vanilla" -> {
                    if (Stream.of(
                        "name",
                        "lore",
                        "enchantments",
                        "custommodeldata"
                    ).anyMatch(s -> !entry.node(s).empty())) {
                        CustomItemLoot customItemLoot = Objects.requireNonNull(entry.get(CustomItemLoot.class));
                        collection.add(customItemLoot);
                    } else {
                        SimpleItemLoot simpleItemLoot = Objects.requireNonNull(entry.get(SimpleItemLoot.class));
                        collection.add(simpleItemLoot);
                    }
                }
                case "plugin" -> {
                    PluginItemLoot pluginItemLoot = Objects.requireNonNull(entry.get(PluginItemLoot.class));
                    collection.add(pluginItemLoot);
                }
                case "command" -> {
                    CommandLoot commandLoot = Objects.requireNonNull(entry.get(CommandLoot.class));
                    collection.add(commandLoot);
                }
                case "table" -> {
                    TableLoot tableLoot = Objects.requireNonNull(entry.get(TableLoot.class));
                    collection.add(tableLoot);
                }
            }
        }
        return collection;
    }

    @Override public void serialize(final Type type, @Nullable final RandomCollection<Loot> obj, final ConfigurationNode node) {

    }
}
