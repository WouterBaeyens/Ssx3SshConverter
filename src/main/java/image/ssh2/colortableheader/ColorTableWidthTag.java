package image.ssh2.colortableheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

/**
 * This number refers to the width of the table. As the height is always 1, it also reflects the nr of defined colors.
 * It seems that the same colors with different alpha channel are counted as one
 */
public class ColorTableWidthTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 2;


    public ColorTableWidthTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "Palette width: " + ByteUtil.printLongWithHex(getConvertedValue());
    }

    public int getConvertedValue() {
        return (int) ByteUtil.convertToLongLE(getRawBytes());
    }

}