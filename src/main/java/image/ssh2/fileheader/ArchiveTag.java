package image.ssh2.fileheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;

/**
 * This tag describes the number of images contained within the ssh file
 */
public class ArchiveTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;

    /**
     * If this number is exceeded, I assume something went wrong.
     */
    private static final int REASONABLE_MAX_NUMBER_OF_IMAGES_IN_FILE = 1000;

    public ArchiveTag(final MappedByteBuffer buffer) throws IOException {
        super(buffer, DEFAULT_SIZE);
        checkAssertions();
    }

    private void checkAssertions(){
        if(getConvertedValue() > 1000) {
            throw new IllegalStateException("Likely something went wrong reading the data: The meta-data describes this ssh-file contains " + getConvertedValue() + " images (anything above "+ REASONABLE_MAX_NUMBER_OF_IMAGES_IN_FILE + " is considered unreasonably high).");
        }
    }

    @Override
    public String getInfo() {
        return "#images: " + getConvertedValue();
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongLE(getBytes());
    }

}
