package image.bmp2.dibheader.tags;

import image.ImgSubComponent;
import util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * the image size (without compression). This is the size of the raw bitmap data; a dummy 0 can be given for BI_RGB bitmaps.
 */
public class RawImageSizeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;
    private final long actualFileSize;

    public RawImageSizeTag(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
        actualFileSize = file.length();
    }

    @Override
    public String getInfo() {
        return "Raw fileSize: " + ByteUtil.printLongWithHex(getConvertedValue());
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongLE(getBytes());
    }

}
