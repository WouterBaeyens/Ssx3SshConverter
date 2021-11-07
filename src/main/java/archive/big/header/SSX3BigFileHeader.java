package archive.big.header;

import image.bmp2.bmpheader.FileSizeTag;

import java.nio.ByteBuffer;

public class SSX3BigFileHeader implements BigFileHeader{

    private final BigFileTypeTag bigFileTypeTag;
    private final FileSizeTag fileSizeTag;
    private final BigNumberOfEntriesTag bigNumberOfEntriesTag;
    private final BigTableSizeTag bigTableSizeTag;

    public SSX3BigFileHeader(final ByteBuffer buffer) {
        this.bigFileTypeTag = new BigFileTypeTag(buffer);
        this.fileSizeTag = new FileSizeTag(buffer);
        this.bigNumberOfEntriesTag = new BigNumberOfEntriesTag(buffer);
        this.bigTableSizeTag = new BigTableSizeTag(buffer);
    }

    public int getNumberOfEntries(){
        return Math.toIntExact(bigNumberOfEntriesTag.getConvertedValue());
    }

    public BigFileTypeTag.BigArchiveType getArchiveType(){
        return bigFileTypeTag.getType().orElse(BigFileTypeTag.BigArchiveType.SSX3_BIG);
    }
}
