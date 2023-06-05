package cc.mewcraft.mewcore.persistent;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

public class DoubleArrayType implements PersistentDataType<byte[], double[]> {

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<double[]> getComplexType() {
        return double[].class;
    }

    @Override
    public byte @NotNull [] toPrimitive(double[] complex, @NotNull PersistentDataAdapterContext context) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(complex.length * 8);
        for (double v : complex) {
            byteBuffer.putDouble(v);
        }
        return byteBuffer.array();
    }

    @Override
    public double @NotNull [] fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(primitive);
        DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer(); // Make DoubleBuffer
        double[] doubles = new double[doubleBuffer.remaining()]; // Make an array of the correct size
        doubleBuffer.get(doubles);
        return doubles;
    }

}
