package cc.mewcraft.adventurelevel.message;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.lucko.helper.messaging.codec.Codec;
import me.lucko.helper.messaging.codec.EncodingException;

import java.util.UUID;

public final class TransientPlayerDataCodec implements Codec<TransientPlayerData> {

    @Override public byte[] encode(final TransientPlayerData message) throws EncodingException {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        // First write out uuid
        out.writeLong(message.uuid().getMostSignificantBits());
        out.writeLong(message.uuid().getLeastSignificantBits());

        // Start write out experience values
        out.writeInt(message.mainXp());
        out.writeInt(message.blockBreakXp());
        out.writeInt(message.breedXp());
        out.writeInt(message.entityDeathXp());
        out.writeInt(message.expBottleXp());
        out.writeInt(message.fishingXp());
        out.writeInt(message.furnaceXp());
        out.writeInt(message.grindstoneXp());
        out.writeInt(message.playerDeathXp());
        out.writeInt(message.villagerTradeXp());

        return out.toByteArray();
    }

    @Override public TransientPlayerData decode(final byte[] buf) throws EncodingException {
        ByteArrayDataInput in = ByteStreams.newDataInput(buf);

        return new TransientPlayerData(
            new UUID(in.readLong(), in.readLong()),
            in.readInt(),
            in.readInt(),
            in.readInt(),
            in.readInt(),
            in.readInt(),
            in.readInt(),
            in.readInt(),
            in.readInt(),
            in.readInt(),
            in.readInt()
        );
    }

}
