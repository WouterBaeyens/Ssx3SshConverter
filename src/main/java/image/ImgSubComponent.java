package image;

import java.io.IOException;
import java.io.RandomAccessFile;

public interface ImgSubComponent {

    long getSize();

    long getStartPos();

    default long getEndPos() {
        return getStartPos() + getSize();
    }

    default byte[] read(RandomAccessFile file, long startPosition) throws IOException {
        return read(file, startPosition, (int) getSize());
    }

    default byte[] read(RandomAccessFile file, long startPosition, int length) throws IOException {
        byte[] data = new byte[length];
        file.seek(startPosition);
        file.read(data);
        return data;
    }

    String getHexData();

    String getInfo();
}
