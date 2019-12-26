package image;

import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class ImgSubComponent {

    private final int componentSize;
    private final long startPosition;

    public ImgSubComponent(final RandomAccessFile file, final long startPosition, final long size) {
        this.startPosition = startPosition;
        this.componentSize = Math.toIntExact(size);
    }

    public long getSize() {
        return componentSize;
    }

    long getStartPos() {

    }

    public long getEndPos() {
        return getStartPos() + getSize();
    }

    public byte[] read(RandomAccessFile file, long startPosition) throws IOException {
        return read(file, startPosition, (int) getSize());
    }

    byte[] read(RandomAccessFile file, long startPosition, int length) throws IOException {
        byte[] data = new byte[length];
        file.seek(startPosition);
        file.read(data);
        return data;
    }

    String getHexData();

    String getInfo();
}
