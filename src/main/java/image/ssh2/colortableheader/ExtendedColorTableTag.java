package image.ssh2.colortableheader;

import image.ImgSubComponent;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * The purpose and info within this tag are completely unknown.
 * The only thing to go off is that it's value is always 0x01 so far
 * <p>
 * The header is always 0x10 bytes so far, so maybe related if read differently?
 * <p>
 * Note: even the name TableType2Tag is just a wild guess
 */
public class ExtendedColorTableTag implements ImgSubComponent {

    private static final long DEFAULT_SIZE = 2;
    private final long size;
    private final long startPosition;
    private final byte[] data;

    public ExtendedColorTableTag(final RandomAccessFile file, final long startPosition, final long size) throws IOException {
        this.startPosition = startPosition;
        this.size = size;
        data = read(file, startPosition);
    }

    @Override
    public long getSize() {
        return size;
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
        return "Extended header size: " + size;
    }
}


