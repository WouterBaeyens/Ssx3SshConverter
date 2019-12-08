package image.ssh2.fileheader;

import image.ImgSubComponent;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This tag describes the number of images contained within the ssh file
 */
public class ArchiveTag implements ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;
    private final long startPosition;
    private final byte[] data;

    public ArchiveTag(final RandomAccessFile file, final long startPosition) throws IOException {
        this.startPosition = startPosition;
        data = read(file, startPosition);
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
        return "#images: " + getConvertedValue();
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongLE(data);
    }

}
