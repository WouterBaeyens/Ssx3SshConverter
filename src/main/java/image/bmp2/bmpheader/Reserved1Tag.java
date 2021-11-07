package image.bmp2.bmpheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Reserved; actual value depends on the application that creates the image
 */
public class Reserved1Tag extends ImgSubComponent {
    private static final long DEFAULT_SIZE = 4;
    private final long actualFileSize;

    public Reserved1Tag(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
        actualFileSize = file.length();
    }

    @Override
    public String getInfo() {
        return "<app-specific data>";
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongLE(getRawBytes());
    }
}
