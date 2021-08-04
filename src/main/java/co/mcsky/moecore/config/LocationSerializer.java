package co.mcsky.moecore.config;

import co.mcsky.moecore.MoeCore;
import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class LocationSerializer implements TypeSerializer<Location> {

    public static final LocationSerializer INSTANCE = new LocationSerializer();

    @Override
    public Location deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final int x = node.node("x").getInt();
        final int y = node.node("y").getInt();
        final int z = node.node("z").getInt();
        final String world = node.node("world").getString("null");
        return new Location(MoeCore.plugin.getServer().getWorld(world), x, y, z);
    }

    @Override
    public void serialize(Type type, @Nullable Location obj, ConfigurationNode node) throws SerializationException {
        Preconditions.checkNotNull(obj);
        node.node("x").set(obj.getBlockX());
        node.node("y").set(obj.getBlockY());
        node.node("z").set(obj.getBlockZ());
        node.node("world").set(obj.getWorld().getName());
    }
}
