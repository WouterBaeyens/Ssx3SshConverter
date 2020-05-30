package image.bmp2.bmpheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * The offset, i.e. starting address, of the byte where the bitmap image data (pixel array) can be found
 */
public class ImagePointerTag extends ImgSubComponent {
    private static final long DEFAULT_SIZE = 4;

    public ImagePointerTag(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        String info = getConvertedValue() + "/0x" + Long.toHexString(getConvertedValue());
        return "Pixel array location: " + info;
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongLE(getBytes());
    }
}
