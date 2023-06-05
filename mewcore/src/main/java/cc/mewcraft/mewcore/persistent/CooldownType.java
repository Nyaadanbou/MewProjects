package cc.mewcraft.mewcore.persistent;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.lucko.helper.cooldown.Cooldown;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class CooldownType implements PersistentDataType<byte[], Cooldown> {

    @Override public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override public @NotNull Class<Cooldown> getComplexType() {
        return Cooldown.class;
    }

    @Override public byte @NotNull [] toPrimitive(final @NotNull Cooldown complex, final @NotNull PersistentDataAdapterContext context) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeLong(complex.getLastTested().orElse(0L));
        out.writeLong(complex.getTimeout());
        return out.toByteArray();
    }

    @Override public @NotNull Cooldown fromPrimitive(final byte @NotNull [] primitive, final @NotNull PersistentDataAdapterContext context) {
        ByteArrayDataInput in = ByteStreams.newDataInput(primitive);
        long lastTested = in.readLong();
        long timeout = in.readLong();
        Cooldown cooldown = Cooldown.of(timeout, TimeUnit.MILLISECONDS);
        cooldown.setLastTested(lastTested);
        return cooldown;
    }

}
