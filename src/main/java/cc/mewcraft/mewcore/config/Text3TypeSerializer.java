package cc.mewcraft.mewcore.config;

import com.google.gson.JsonElement;
import me.lucko.helper.gson.GsonProvider;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class Text3TypeSerializer implements TypeSerializer<Component> {

    @Override
    public Component deserialize(Type type, ConfigurationNode node) throws SerializationException {
        JsonElement json = node.get(JsonElement.class);
        return GsonProvider.standard().fromJson(json, type);
    }

    @Override
    public void serialize(Type type, @Nullable Component obj, ConfigurationNode node) throws SerializationException {
        JsonElement element = GsonProvider.standard().toJsonTree(obj, type);
        node.set(JsonElement.class, element);
    }
}
