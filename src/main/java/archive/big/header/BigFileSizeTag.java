package archive.big.header;

import image.ImgSubComponent;
import util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * This tag describes the size of the full ssh file in bytes.
 */
public class BigFileSizeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;
    private final long actualFileSize;

    public BigFileSizeTag(final ByteBuffer byteBuffer) {
        super(byteBuffer, DEFAULT_SIZE);
        actualFileSize = byteBuffer.limit();
    }

    public BigFileSizeTag(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
        actualFileSize = file.length();
    }

    @Override
    public String getInfo() {
        String info = ByteUtil.printLongWithHex(getConvertedValue());
        if (isCompressed()) {
            info += " warning: compressed file!! - actual size: " + actualFileSize + "(0x" + Long.toHexString(actualFileSize) + ")";
        } else if (getConvertedValue() < actualFileSize) {
            info += " warning: does NOT match actual file size!! " + actualFileSize + "(0x" + Long.toHexString(actualFileSize) + ")";
        }
        return "FileSize: " + info;
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongLE(getBytes());
    }

    public boolean isCompressed() {
        return actualFileSize < getConvertedValue();
    }
}
