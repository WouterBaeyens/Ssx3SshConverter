package image.ssh2.fileheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * This tag describes: the image name & location of the image header
 * of an image inside the ssh
 */
public class ImageHeaderInfoTag extends ImgSubComponent {

    private static final int NAME_SIZE = 4;
    private static final int HEADER_LOCATION_SIZE = 4;
    private static final int DEFAULT_SIZE = NAME_SIZE + HEADER_LOCATION_SIZE;

    public ImageHeaderInfoTag(final ByteBuffer file) {
        super(file, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "Img:(nameTag=" + getName() + "; headerLocation=" + ByteUtil.printLongWithHex(getHeaderLocation()) + ")";
    }

    public String getName() {
        return new String(Arrays.copyOf(getRawBytes(), NAME_SIZE));
    }

    public long getHeaderLocation() {
        return ByteUtil.convertToLongLE(Arrays.copyOfRange(getRawBytes(), 4, 8));
    }
}
