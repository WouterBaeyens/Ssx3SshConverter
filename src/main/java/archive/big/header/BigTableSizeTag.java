package archive.big.header;

import image.ImgSubComponent;
import util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * This tag describes the size of the full ssh file in bytes.
 */
public class BigTableSizeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;
    private final long actualFileSize;

    public BigTableSizeTag(final ByteBuffer byteBuffer) {
        super(byteBuffer, DEFAULT_SIZE);
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
        return ByteUtil.convertToLongBE(getBytes());
    }

    public boolean isCompressed() {
        return actualFileSize < getConvertedValue();
    }
}
