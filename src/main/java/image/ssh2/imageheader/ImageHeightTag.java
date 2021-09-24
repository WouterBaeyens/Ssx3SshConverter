package image.ssh2.imageheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * This tag describes the height of the image.
 */
public class ImageHeightTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 2;

    public ImageHeightTag(final ByteBuffer sshFileBuffer) throws IOException {
        super(sshFileBuffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "ImageHeight: " + ByteUtil.printLongWithHex(getConvertedValue());
    }

    public int getConvertedValue() {
        return (int) ByteUtil.convertToLongLE(getBytes());
    }

}
