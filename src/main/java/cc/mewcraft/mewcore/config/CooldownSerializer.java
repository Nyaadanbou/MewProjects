package cc.mewcraft.mewcore.config;

import com.google.common.base.Preconditions;
import me.lucko.helper.cooldown.Cooldown;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

public class CooldownSerializer implements TypeSerializer<Cooldown> {

    @Override
    public Cooldown deserialize(Type type, ConfigurationNode node) throws SerializationException {
        long lastTested = node.node("lastTested").getLong();
        long timeout = node.node("timeout").getLong();

        Cooldown c = Cooldown.of(timeout, TimeUnit.MILLISECONDS);
        c.setLastTested(lastTested);
        return c;
    }

    @Override
    public void serialize(Type type, @Nullable Cooldown obj, ConfigurationNode node) throws SerializationException {
        Preconditions.checkNotNull(obj);
        node.node("lastTested").set(obj.getLastTested().orElse(0));
        node.node("timeout").set(obj.getTimeout());
    }
}
