package image.ssh2.colortableheader;

import image.ImgSubComponent;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * My initial guess would be that this number refers to the nr of defined colors. (the nr is lower when size is lower)
 * It seems that the same colors with different alpha channel are counted as one
 * To be verified
 */
public class ColorTableEntriesTag implements ImgSubComponent {

    private static final long DEFAULT_SIZE = 2;
    private final long startPosition;
    private final byte[] data;

    public ColorTableEntriesTag(final RandomAccessFile file, final long startPosition) throws IOException {
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
        String info = ByteUtil.printLongWithHex(getConvertedValue());
        return "#Colors: " + info;
    }

    public int getConvertedValue() {
        return (int) ByteUtil.convertToLongLE(data);
    }

}