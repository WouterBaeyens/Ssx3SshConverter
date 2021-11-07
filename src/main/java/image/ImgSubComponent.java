package image;

import util.FileUtil;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.Optional;

public abstract class ImgSubComponent {

    private final int componentSize;
    private final long startPosition;
    private final byte[] data;
    private final ByteOrder byteOrder;

    public ImgSubComponent(final RandomAccessFile file, final long startPosition, final long size) throws IOException {
        this.startPosition = startPosition;
        this.componentSize = Math.toIntExact(size);
        this.data = FileUtil.read(file, startPosition, componentSize);
        this.byteOrder = ByteOrder.BIG_ENDIAN;
    }


    public ImgSubComponent(final ByteBuffer buffer, final long size) {
        this(buffer, size, ByteOrder.BIG_ENDIAN);
    }

    public ImgSubComponent(final ByteBuffer buffer, final long size, ByteOrder byteOrder) {
        this(buffer, buffer.position(), size, byteOrder);
    }

    public ImgSubComponent(final ByteBuffer buffer, final byte stopByte) {
        this.startPosition = buffer.position();
        this.data = FileUtil.readUntilStop(buffer, stopByte);
        this.componentSize = data.length;
        this.byteOrder = ByteOrder.BIG_ENDIAN;
    }

    private ImgSubComponent(final ByteBuffer buffer, final long startPosition, final long size, final ByteOrder byteOrder) {
        this.startPosition = startPosition;
        this.componentSize = Math.toIntExact(size);
        this.data = FileUtil.readAndConsume(buffer, componentSize);
        this.byteOrder = byteOrder;
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

    public byte[] getRawBytes() {
        return data;
    }

    public byte[] getBytesBE(){
        return byteOrder.toBigEndian.apply(getRawBytes());
    }

    public String getHexData() {
        return PrintUtil.toHexString(false, data);
    }

    public abstract String getInfo();

    @Override
    public String toString() {
        return getInfo();
    }
}
