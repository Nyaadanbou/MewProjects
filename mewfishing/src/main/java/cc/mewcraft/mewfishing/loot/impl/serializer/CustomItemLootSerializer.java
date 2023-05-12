package cc.mewcraft.mewfishing.loot.impl.serializer;

import cc.mewcraft.mewfishing.loot.impl.loot.CustomItemLoot;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;

public class CustomItemLootSerializer implements TypeSerializer<CustomItemLoot> {
    @Override public CustomItemLoot deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        CustomItemLoot.CustomLootItemBuilder builder = new CustomItemLoot
            .CustomLootItemBuilder(Objects.requireNonNull(node.node("id").getString(), node.path() + ": id is null"))
            .amount(node.node("amount").getString("1"))
            .weight(node.node("weight").getDouble(1D))
            .conditions(node.node("conditions").get(new TypeToken<>() {}))
            .name(node.node("name").getString())
            .lore(node.node("lore").getList(String.class))
            .customModelData(node.node("custommodeldata").getInt(0));
        ConfigurationNode enchantmentNode = node.node("enchantments");
        if (enchantmentNode.isMap()) {
            Set<Object> keys = enchantmentNode.childrenMap().keySet();
            for (Object key : keys) {
                builder.enchantment(key.toString(), Objects.requireNonNull(enchantmentNode.node(key).getString()));
            }
        }
        return builder.build();
    }

    @Override public void serialize(final Type type, @Nullable final CustomItemLoot obj, final ConfigurationNode node) {

    }
}
