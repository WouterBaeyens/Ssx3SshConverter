package image.ssh2.colortableheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * This tag describes the full size of the table.
 * full size = table header + table data.
 */
public class ColorTableSizeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 3;

    public ColorTableSizeTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "TableSize: " + ByteUtil.printLongWithHex(getConvertedValue());
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongLE(getBytes());
    }

}