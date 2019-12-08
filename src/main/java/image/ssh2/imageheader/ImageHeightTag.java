package image.ssh2.imageheader;

import image.ImgSubComponent;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This tag describes the height of the image.
 */
public class ImageHeightTag implements ImgSubComponent {

    private static final long DEFAULT_SIZE = 2;
    private final long startPosition;
    private final byte[] data;

    public ImageHeightTag(final RandomAccessFile file, final long startPosition) throws IOException {
        this.startPosition = startPosition;
        data = read(file, getStartPos());
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
        String info = getConvertedValue() + "/0x" + Long.toHexString(getConvertedValue());
        return "ImageHeight: " + info;
    }

    public int getConvertedValue() {
        return (int) ByteUtil.convertToLongLE(data);
    }

}
