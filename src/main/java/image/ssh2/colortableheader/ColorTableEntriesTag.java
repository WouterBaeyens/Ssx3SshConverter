package image.ssh2.colortableheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

/**
 * My initial guess would be that this number refers to the nr of defined colors. (the nr is lower when size is lower)
 * To be verified
 */
public class ColorTableEntriesTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 2;
    private final int expectedAmount;

    public ColorTableEntriesTag(final ByteBuffer buffer, final int expectedAmount) {
        super(buffer, DEFAULT_SIZE);
        this.expectedAmount = expectedAmount;
    }

    @Override
    public String getInfo() {
        String info = ByteUtil.printLongWithHex(getConvertedValue());
        if (!isCopyOfFirstAmount()) {
            info += " warning: does NOT match first amount field!! " + ByteUtil.printLongWithHex(expectedAmount);
        }
        return "#Colors : " + info;
    }

    private boolean isCopyOfFirstAmount() {
        return getConvertedValue() == expectedAmount;
    }

    public int getConvertedValue() {
        return (int) ByteUtil.convertToLongLE(getRawBytes());
    }

}