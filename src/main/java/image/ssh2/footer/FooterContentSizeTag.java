package image.ssh2.footer;

import image.ImgSubComponent;
import util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * This tag describes the size of the full ssh file.
 */
public class FooterContentSizeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 3;

    public FooterContentSizeTag(final ByteBuffer buffer) throws IOException {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "FooterSize: " + ByteUtil.printLongWithHex(getConvertedValue());
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongLE(getBytes());
    }

}
