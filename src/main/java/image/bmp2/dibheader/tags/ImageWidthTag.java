package image.bmp2.dibheader.tags;

import image.ImgSubComponent;
import util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Width of the bitmap in pixels.
 */
public class ImageWidthTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;

    public ImageWidthTag(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        String info = getConvertedValue() + "/0x" + Long.toHexString(getConvertedValue());
        return "ImageWidth: " + info;
    }

    public int getConvertedValue() {
        return (int) ByteUtil.convertToLongLE(getBytes());
    }

}
