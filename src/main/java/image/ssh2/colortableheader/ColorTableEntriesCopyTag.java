package image.ssh2.colortableheader;

import image.ImgSubComponent;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * My initial guess would be that this number refers to the nr of defined colors. (the nr is lower when size is lower)
 * To be verified
 */
public class ColorTableEntriesCopyTag implements ImgSubComponent {

    private static final long DEFAULT_SIZE = 2;
    private final long startPosition;
    private final byte[] data;

    private final int expectedAmount;

    public ColorTableEntriesCopyTag(final RandomAccessFile file, final long startPosition, final int expectedAmount) throws IOException {
        this.startPosition = startPosition;
        data = read(file, getStartPos());
        this.expectedAmount = expectedAmount;
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
        if (!isCopyOfFirstAmount()) {
            info += " warning: does NOT match first amount field!! " + ByteUtil.printLongWithHex(expectedAmount);
        }
        return "(copy) #Colors : " + info;
    }

    private boolean isCopyOfFirstAmount() {
        return getConvertedValue() == expectedAmount;
    }

    public int getConvertedValue() {
        return (int) ByteUtil.convertToLongLE(data);
    }

}