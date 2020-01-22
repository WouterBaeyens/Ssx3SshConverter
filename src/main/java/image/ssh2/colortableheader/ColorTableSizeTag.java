package image.ssh2.colortableheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This tag describes the full size of the table.
 * full size = table header + table data.
 */
public class ColorTableSizeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 3;

    public ColorTableSizeTag(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        String info = getConvertedValue() + "/0x" + Long.toHexString(getConvertedValue());
        return "TableSize: " + info;
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongLE(getBytes());
    }

}