package image.ssh2.fileheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This tag describes the number of images contained within the ssh file
 */
public class ArchiveTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;

    public ArchiveTag(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "#images: " + getConvertedValue();
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongLE(getBytes());
    }

}
