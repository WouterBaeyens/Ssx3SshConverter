package image.ssh2.imageheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * This tag describes the full size of the image.
 * full size = image header + image pixels.
 */
public class ImageSizeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 3;

    public ImageSizeTag(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
    }

    public ImageSizeTag(final ByteBuffer file) throws IOException {
        super(file, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "ImageSize: " + ByteUtil.printLongWithHex(getConvertedValue());
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongLE(getBytes());
    }

}