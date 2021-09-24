package util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

public class FileUtil {

    /**
     * Read the data like with absolute positioning. The position is not updated for the next read
     */
    public static byte[] read(RandomAccessFile file, long startPosition, int length) throws IOException {
        byte[] data = new byte[length];
        file.seek(startPosition);
        file.read(data);
        return data;
    }

    /**
     * Reads the data like a stream - updating the position for the next read
     */
    public static byte[] readAndConsume(ByteBuffer file, int length) {
        byte[] data = new byte[length];
        file.get(data);
        return data;
    }
}
