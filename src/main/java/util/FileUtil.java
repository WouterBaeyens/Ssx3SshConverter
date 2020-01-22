package util;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileUtil {

    public static byte[] read(RandomAccessFile file, long startPosition, int length) throws IOException {
        byte[] data = new byte[length];
        file.seek(startPosition);
        file.read(data);
        return data;
    }
}
