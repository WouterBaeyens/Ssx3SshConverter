package image.ssh2.fileheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

/**
 * This tag describes the size of the full ssh file.
 */
public class TotalFileSizeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;
    private final long actualFileSize;

    public TotalFileSizeTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
        actualFileSize = buffer.remaining() + buffer.position();
    }

    @Override
    public String getInfo() {
        String info = ByteUtil.printLongWithHex(getConvertedValue());
        if (isCompressed()) {
            info += " warning: compressed file!! - actual size: " + actualFileSize + "(0x" + Long.toHexString(actualFileSize) + ")";
        } else if (getConvertedValue() < actualFileSize) {
            info += " warning: does NOT match actual file size!! " + actualFileSize + "(0x" + Long.toHexString(actualFileSize) + ")";
        }
        return "TotalFileSize: " + info;
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongLE(getRawBytes());
    }

    public boolean isCompressed() {
        return actualFileSize < getConvertedValue();
    }
}
