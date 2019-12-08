package image.ssh2.colortableheader;

import image.ImgSubComponent;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This tag describes the full size of the table.
 * full size = table header + table data.
 */
public class ColorTableSizeTag implements ImgSubComponent {

    private static final long DEFAULT_SIZE = 3;
    private final long startPosition;
    private final byte[] data;

    public ColorTableSizeTag(final RandomAccessFile file, final long startPosition) throws IOException {
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
        return "TableSize: " + info;
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongLE(data);
    }

}