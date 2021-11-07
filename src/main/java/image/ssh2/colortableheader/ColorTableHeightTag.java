package image.ssh2.colortableheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

/**
 * This number refers to the height of the table. It seems the height is always 1.
 */
public class ColorTableHeightTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 2;

    public ColorTableHeightTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "Palette height: " + ByteUtil.printLongWithHex(getConvertedValue());
    }

    public int getConvertedValue() {
        return (int) ByteUtil.convertToLongLE(getRawBytes());
    }
}
