package co.mcsky.moecore.config;

import com.google.common.base.Preconditions;
import io.leangen.geantyref.TypeToken;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class BukkitTypeSerializer implements TypeSerializer<ConfigurationSerializable> {

    private static final TypeToken<Map<String, Object>> TYPE = new TypeToken<>() {};

    private static void deserializeChildren(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                try {
                    //noinspection unchecked
                    Map<String, Object> value = (Map) entry.getValue();

                    deserializeChildren(value);

                    if (value.containsKey("==")) {
                        entry.setValue(ConfigurationSerialization.deserializeObject(value));
                    }

                } catch (Exception e) {
                    // ignore
                }
            }

            if (entry.getValue() instanceof Number) {
                double doubleVal = ((Number) entry.getValue()).doubleValue();
                int intVal = (int) doubleVal;
                long longVal = (long) doubleVal;

                if (intVal == doubleVal) {
                    entry.setValue(intVal);
                } else if (longVal == doubleVal) {
                    entry.setValue(longVal);
                } else {
                    entry.setValue(doubleVal);
                }
            }
        }
    }

    @Override
    public ConfigurationSerializable deserialize(Type type, ConfigurationNode from) throws SerializationException {
        Map<String, Object> map = from.get(TYPE);
        Preconditions.checkNotNull(map, "map");
        deserializeChildren(map);
        return ConfigurationSerialization.deserializeObject(map);
    }

    @Override
    public void serialize(Type type, @Nullable ConfigurationSerializable from, ConfigurationNode to) throws SerializationException {
        Map<String, Object> serialized = from.serialize();

        Map<String, Object> map = new LinkedHashMap<>(serialized.size() + 1);
        map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(from.getClass()));
        map.putAll(serialized);

        to.set(map);
    }
}
