package co.mcsky.moecore.config;

import me.lucko.helper.text3.Text;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class ColoredStringTypeSerializer implements TypeSerializer<String> {

    @Override
    public String deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return Text.colorize(node.getString());
    }

    @Override
    public void serialize(Type type, @Nullable String obj, ConfigurationNode node) throws SerializationException {
        node.set(Text.decolorize(obj));
    }
}
