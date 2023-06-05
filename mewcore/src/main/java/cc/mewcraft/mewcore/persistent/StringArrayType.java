package cc.mewcraft.mewcore.persistent;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class StringArrayType implements PersistentDataType<byte[], String[]> {

    private final Charset charset;

    public StringArrayType(Charset charset) {
        this.charset = charset;
    }

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<String[]> getComplexType() {
        return String[].class;
    }

    @Override
    public byte @NotNull [] toPrimitive(String[] strings, @NotNull PersistentDataAdapterContext context) {
        byte[][] allStringBytes = new byte[strings.length][];
        int total = 0;
        for (int i = 0; i < allStringBytes.length; i++) {
            byte[] bytes = strings[i].getBytes(this.charset);
            allStringBytes[i] = bytes;
            total += bytes.length;
        }

        ByteBuffer buffer = ByteBuffer.allocate(total + allStringBytes.length * 4); // stores integers
        for (byte[] bytes : allStringBytes) {
            buffer.putInt(bytes.length);
            buffer.put(bytes);
        }

        return buffer.array();
    }

    @Override
    public @NotNull String @NotNull [] fromPrimitive(byte @NotNull [] bytes, @NotNull PersistentDataAdapterContext context) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        ArrayList<String> list = new ArrayList<>();

        while (buffer.remaining() > 0) {
            if (buffer.remaining() < 4)
                break;
            int stringLength = buffer.getInt();
            if (buffer.remaining() < stringLength)
                break;

            byte[] stringBytes = new byte[stringLength];
            buffer.get(stringBytes);

            list.add(new String(stringBytes, this.charset));
        }

        return list.toArray(new String[0]);
    }
}
