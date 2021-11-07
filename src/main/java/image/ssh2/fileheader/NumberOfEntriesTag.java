package image.ssh2.fileheader;

import image.AbstractAmountTag;
import image.ByteOrder;

import java.nio.ByteBuffer;

/**
 * This tag describes the number of images contained within the ssh file
 */
public class NumberOfEntriesTag extends AbstractAmountTag {

    private static final int DEFAULT_SIZE = 4;

    /**
     * If this number is exceeded, I assume something went wrong.
     */
    private static final int REASONABLE_MAX_NUMBER_OF_IMAGES_IN_FILE = 1000;

    public NumberOfEntriesTag(final ByteBuffer buffer, final ByteOrder byteOrder) {
        super(buffer, DEFAULT_SIZE, "#images", byteOrder);
        checkAssertions();
    }

    private void checkAssertions(){
        if(getConvertedValue() > REASONABLE_MAX_NUMBER_OF_IMAGES_IN_FILE) {
            throw new IllegalStateException("Likely something went wrong reading the data: The meta-data describes this ssh-file contains " + getConvertedValue() + " images (anything above "+ REASONABLE_MAX_NUMBER_OF_IMAGES_IN_FILE + " is considered unreasonably high).");
        }
    }

    public static class Reader extends AbstractAmountTag.Reader<Reader> {

        public NumberOfEntriesTag read(ByteBuffer buffer){
            return new NumberOfEntriesTag(buffer, getOrder());
        }
    }
}
