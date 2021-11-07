package image.bmp2.dibheader.tags;

import image.ImgSubComponent;
import util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Number of bits per pixel
 */
public class BitsPerPixelTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 2;

    public BitsPerPixelTag(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "# Bits/pixel: " + ByteUtil.printLongWithHex(getConvertedValue());
    }

    public int getConvertedValue() {
        return (int) ByteUtil.convertToLongLE(getRawBytes());
    }

}
