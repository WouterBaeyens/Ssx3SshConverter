package archive.big.header;

import image.AbstractAmountTag;
import image.ByteOrder;

import java.nio.ByteBuffer;

public class RefPackBigFileHeader implements BigFileHeader {

    /**
     * If this number is exceeded, I assume something went wrong.
     */
    private static final int REASONABLE_MAX_NUMBER_ENTRIES_IN_BIG_FILE = 2000;


    private final BigFileTypeTag bigFileTypeTag;

    /**
     * This tag describes the size of all the data containing info on the files contained within the .big file
     *      = bigNumberOfEntriesTag.size + size of List<BigSubFileInfoTag>
     **/
    private final AbstractAmountTag bigTableSizeIncludingEntriestag;
    private final AbstractAmountTag bigNumberOfEntriesTag;

    public RefPackBigFileHeader(final ByteBuffer buffer) {
        this.bigFileTypeTag = new BigFileTypeTag(buffer);
        this.bigTableSizeIncludingEntriestag = new AbstractAmountTag.Reader<>()
                .withName("subFileInfoSize")
                .withSize(2)
                .read(buffer);

        this.bigNumberOfEntriesTag = new AbstractAmountTag.Reader<>()
                .withName("bigNumberOfEntriesTag")
                .withSize(2)
                .read(buffer);
        assertValid();
    }

    public BigFileTypeTag.BigArchiveType getArchiveType(){
        return bigFileTypeTag.getType().orElse(BigFileTypeTag.BigArchiveType.SSX3_BIG);
    }

    private void assertValid() {
        bigFileTypeTag.getType()
                .filter(type -> type == BigFileTypeTag.BigArchiveType.REF_PACK_ARCHIVE)
                .orElseThrow(() -> new IllegalArgumentException("RefPack has unexpected type: " + bigFileTypeTag));
        if(bigNumberOfEntriesTag.getConvertedValue() > REASONABLE_MAX_NUMBER_ENTRIES_IN_BIG_FILE){
            throw new IllegalStateException("Likely something went wrong reading the data: The meta-data describes this .big file contains " + bigNumberOfEntriesTag.getConvertedValue() + " files (anything above "+ REASONABLE_MAX_NUMBER_ENTRIES_IN_BIG_FILE + " is considered unreasonably high).");
        }
    }

    @Override
    public int getNumberOfEntries() {
        return Math.toIntExact(bigNumberOfEntriesTag.getConvertedValue());
    }
}
