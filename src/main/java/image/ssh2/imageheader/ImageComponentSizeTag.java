package image.ssh2.imageheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * This tag describes the full size of the image component.
 * full size = image header + image pixels.
 */
public class ImageComponentSizeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 3;

    public ImageComponentSizeTag(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
    }

    public ImageComponentSizeTag(final ByteBuffer file) {
        super(file, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "ImageComponentSize: " + ByteUtil.printLongWithHex(getConvertedValue());
    }

    public int getConvertedValue() {
        return Math.toIntExact(ByteUtil.convertToLongLE(getRawBytes()));
    }

}