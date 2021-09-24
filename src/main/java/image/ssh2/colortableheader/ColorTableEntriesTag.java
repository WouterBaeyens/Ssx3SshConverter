package image.ssh2.colortableheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * My initial guess would be that this number refers to the nr of defined colors. (the nr is lower when size is lower)
 * It seems that the same colors with different alpha channel are counted as one
 * To be verified
 */
public class ColorTableEntriesTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 2;


    public ColorTableEntriesTag(final ByteBuffer buffer) throws IOException {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        String info = ByteUtil.printLongWithHex(getConvertedValue());
        return "#Colors: " + info;
    }

    public int getConvertedValue() {
        return (int) ByteUtil.convertToLongLE(getBytes());
    }

}