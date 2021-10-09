package util;

import java.nio.ByteBuffer;
import java.util.Objects;

public class JavaCompatibility {

    public static void get(final ByteBuffer byteBuffer, byte[] dst){
        get(byteBuffer, byteBuffer.position(), dst, 0, dst.length);
    }

    public static void get(final ByteBuffer byteBuffer, int index, byte[] dst){
        get(byteBuffer, index, dst, 0, dst.length);
    }

    public static void get(final ByteBuffer byteBuffer, int index, byte[] dst, int offset, int length) {
        Objects.checkFromIndexSize(index, length, byteBuffer.limit());
        Objects.checkFromIndexSize(offset, length, dst.length);
        int end = offset + length;
        for (int i = offset, j = index; i < end; i++, j++)
            dst[i] = byteBuffer.get(j);
    }
}
