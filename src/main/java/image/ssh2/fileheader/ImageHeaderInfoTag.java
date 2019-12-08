package image.ssh2.fileheader;

import image.ImgSubComponent;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * This tag describes: the image name & location of the image header
 * of an image inside the ssh
 */
public class ImageHeaderInfoTag implements ImgSubComponent {

    private static final long DEFAULT_SIZE = 8;
    private final long startPosition;
    private final byte[] data;

    public ImageHeaderInfoTag(final RandomAccessFile file, final long startPosition) throws IOException {
        this.startPosition = startPosition;
        data = read(file, startPosition);
    }

    @Override
    public long getSize() {
        return DEFAULT_SIZE;
    }

    @Override
    public long getStartPos() {
        return startPosition;
    }

    @Override
    public String getHexData() {
        return PrintUtil.toHexString(false, data);
    }

    @Override
    public String getInfo() {
        return "Img:(name=" + getName() + "; headerLocation=" + getHeaderLocation() + "/0x" + Long.toHexString(getHeaderLocation()) + ")";
    }

    public String getName() {
        return new String(Arrays.copyOf(data, 4));
    }

    public long getHeaderLocation() {
        return ByteUtil.convertToLongLE(Arrays.copyOfRange(data, 4, 8));
    }
}
