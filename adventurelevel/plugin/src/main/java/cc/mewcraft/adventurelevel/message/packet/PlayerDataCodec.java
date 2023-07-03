package cc.mewcraft.adventurelevel.message.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.lucko.helper.messaging.codec.Codec;
import me.lucko.helper.messaging.codec.EncodingException;

import java.util.UUID;

public final class PlayerDataCodec implements Codec<PlayerDataPacket> {

    @Override public byte[] encode(final PlayerDataPacket message) throws EncodingException {
        @SuppressWarnings("UnstableApiUsage")
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        // write uuid
        out.writeLong(message.uuid().getMostSignificantBits());
        out.writeLong(message.uuid().getLeastSignificantBits());

        // write server
        out.writeUTF(message.server());

        // write timestamp
        out.writeLong(message.timestamp());

        // write experience values
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

    @Override public PlayerDataPacket decode(final byte[] buf) throws EncodingException {
        @SuppressWarnings("UnstableApiUsage")
        ByteArrayDataInput in = ByteStreams.newDataInput(buf);

        return new PlayerDataPacket(
            new UUID(in.readLong(), in.readLong()),
            in.readUTF(),
            in.readLong(),
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
