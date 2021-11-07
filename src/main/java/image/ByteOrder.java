package image;

import java.util.function.Function;

public enum ByteOrder {

    BIG_ENDIAN(bytes -> bytes),
    LITTLE_ENDIAN(ByteOrder::inverse);

    ByteOrder(Function<byte[], byte[]> toBigEndian){
        this.toBigEndian = toBigEndian;
    }

    public Function<byte[], byte[]> toBigEndian;

    private static byte[] inverse(byte[] bytes){
        byte[] inversed = new byte[bytes.length];
        for(int i = 0; i < bytes.length -1; i++)
        {
            inversed[bytes.length -1 - i] = bytes[i];
        }
        return inversed;
    }
}
