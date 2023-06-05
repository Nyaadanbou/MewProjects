package cc.mewcraft.mewcore.persistent;

import me.lucko.helper.cooldown.Cooldown;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class CooldownType implements PersistentDataType<byte[], Cooldown> {

    @Override public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override public @NotNull Class<Cooldown> getComplexType() {
        return Cooldown.class;
    }

    @Override public byte @NotNull [] toPrimitive(@NotNull final Cooldown complex, @NotNull final PersistentDataAdapterContext context) {
        return new byte[0];
    }

    @Override public @NotNull Cooldown fromPrimitive(final byte @NotNull [] primitive, @NotNull final PersistentDataAdapterContext context) {
        return null;
    }

}
