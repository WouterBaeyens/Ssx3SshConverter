package image.ssh2.fileheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.Arrays;

/**
 * This tag describes: the image name & location of the image header
 * of an image inside the ssh
 */
public class ImageHeaderInfoTag extends ImgSubComponent {

    private static final int NAME_SIZE = 4;
    private static final int HEADER_LOCATION_SIZE = 4;
    private static final int DEFAULT_SIZE = NAME_SIZE + HEADER_LOCATION_SIZE;

    public ImageHeaderInfoTag(final ByteBuffer file) throws IOException {
        super(file, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "Img:(name=" + getName() + "; headerLocation=" + getHeaderLocation() + "/0x" + Long.toHexString(getHeaderLocation()) + ")";
    }

    public String getName() {
        return new String(Arrays.copyOf(getBytes(), NAME_SIZE));
    }

    public long getHeaderLocation() {
        return ByteUtil.convertToLongLE(Arrays.copyOfRange(getBytes(), 4, 8));
    }
}
