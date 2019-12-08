package image.ssh2.fileheader;

import image.ImgSubComponent;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This tag describes the size of the full ssh file.
 */
public class FileSizeTag implements ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;
    private final long startPosition;
    private final byte[] data;
    private final long actualFileSize;

    public FileSizeTag(final RandomAccessFile file, final long startPosition) throws IOException {
        this.startPosition = startPosition;
        data = read(file, getStartPos());
        actualFileSize = file.length();
    }

    @Override
    public long getSize() {
        return DEFAULT_SIZE;
    }

    @Override
    public long getStartPos() {
        return startPosition;
    }

    @Override
    public String getHexData() {
        return PrintUtil.toHexString(false, data);
    }

    @Override
    public String getInfo() {
        String info = getConvertedValue() + "/0x" + Long.toHexString(getConvertedValue());
        if (isCompressed()) {
            info += " warning: compressed file!! - actual size: " + actualFileSize + "(0x" + Long.toHexString(actualFileSize) + ")";
        } else if (getConvertedValue() < actualFileSize) {
            info += " warning: does NOT match actual file size!! " + actualFileSize + "(0x" + Long.toHexString(actualFileSize) + ")";
        }
        return "FileSize: " + info;
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongLE(data);
    }

    public boolean isCompressed() {
        return actualFileSize < getConvertedValue();
    }
}
