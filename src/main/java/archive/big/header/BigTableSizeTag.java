package archive.big.header;

import image.ImgSubComponent;
import util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * This tag describes end address of the subFileInfo data.
 */
public class BigTableSizeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;
    private final long actualFileSize;

    public BigTableSizeTag(final ByteBuffer byteBuffer) {
        super(byteBuffer, DEFAULT_SIZE);
        actualFileSize = byteBuffer.limit();
    }

    public BigTableSizeTag(final ByteBuffer byteBuffer, int size) {
        super(byteBuffer, size);
        actualFileSize = byteBuffer.limit();
    }

    public BigTableSizeTag(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
        actualFileSize = file.length();
    }

    @Override
    public String getInfo() {
        return "TableSize: " + getConvertedValue();
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongBE(getRawBytes());
    }

    public boolean isCompressed() {
        return actualFileSize < getConvertedValue();
    }
}
