package archive.big.header;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

/**
 * This tag describes the number of files contained within the .BIG file in LE
 */
public class BigNumberOfEntriesTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;

    /**
     * If this number is exceeded, I assume something went wrong.
     */
    private static final int REASONABLE_MAX_NUMBER_ENTRIES_IN_BIG_FILE = 2000;

    public BigNumberOfEntriesTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
        checkAssertions();
    }

    private void checkAssertions(){
        if(getConvertedValue() > REASONABLE_MAX_NUMBER_ENTRIES_IN_BIG_FILE) {
            throw new IllegalStateException("Likely something went wrong reading the data: The meta-data describes this .big file contains " + getConvertedValue() + " files (anything above "+ REASONABLE_MAX_NUMBER_ENTRIES_IN_BIG_FILE + " is considered unreasonably high).");
        }
    }

    @Override
    public String getInfo() {
        return "#entries: " + getConvertedValue();
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongBE(getRawBytes());
    }

}
