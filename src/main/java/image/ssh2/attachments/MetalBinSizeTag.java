package image.ssh2.attachments;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

/**
 * This tag describes the size of the full ssh file.
 */
public class MetalBinSizeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 3;

    public MetalBinSizeTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "Attachment size: " + ByteUtil.printLongWithHex(getConvertedValue());
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongLE(getRawBytes());
    }

}
