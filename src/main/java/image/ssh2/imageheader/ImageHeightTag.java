package image.ssh2.imageheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This tag describes the height of the image.
 */
public class ImageHeightTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 2;

    public ImageHeightTag(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        String info = getConvertedValue() + "/0x" + Long.toHexString(getConvertedValue());
        return "ImageHeight: " + info;
    }

    public int getConvertedValue() {
        return (int) ByteUtil.convertToLongLE(getBytes());
    }

}
