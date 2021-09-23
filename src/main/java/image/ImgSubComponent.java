package image;

import util.FileUtil;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;

public abstract class ImgSubComponent {

    private final int componentSize;
    private final long startPosition;
    private final byte[] data;

    public ImgSubComponent(final RandomAccessFile file, final long startPosition, final long size) throws IOException {
        this.startPosition = startPosition;
        this.componentSize = Math.toIntExact(size);
        this.data = FileUtil.read(file, startPosition, componentSize);
    }


    public ImgSubComponent(final MappedByteBuffer buffer, final long size) throws IOException {
        this(buffer, buffer.position(), size);
    }

    public ImgSubComponent(final MappedByteBuffer buffer, final long startPosition, final long size) throws IOException {
        this.startPosition = startPosition;
        this.componentSize = Math.toIntExact(size);
        this.data = FileUtil.readAndConsume(buffer, componentSize);
    }

    public int getSize() {
        return componentSize;
    }

    public long getStartPos() {
        return startPosition;
    }

    public long getEndPos() {
        return getStartPos() + getSize();
    }

    public byte[] getBytes() {
        return data;
    }

    public String getHexData() {
        return PrintUtil.toHexString(false, data);
    }

    public abstract String getInfo();
}
