package archive.big;

import archive.big.header.BigFileTypeTag;
import archive.big.header.BigNumberOfEntriesTag;
import archive.big.header.BigTableSizeTag;
import image.bmp2.bmpheader.FileSizeTag;

import java.nio.ByteBuffer;

public class BigFileHeader {

    private final BigFileTypeTag bigFileTypeTag;
    private final FileSizeTag fileSizeTag;
    private final BigNumberOfEntriesTag bigNumberOfEntriesTag;
    private final BigTableSizeTag bigTableSizeTag;

    public BigFileHeader(ByteBuffer buffer){
        this.bigFileTypeTag = new BigFileTypeTag(buffer);
        this.fileSizeTag = new FileSizeTag(buffer);
        this.bigNumberOfEntriesTag = new BigNumberOfEntriesTag(buffer);
        this.bigTableSizeTag = new BigTableSizeTag(buffer);
    }

    public int getNumberOfEntries(){
        return Math.toIntExact(bigNumberOfEntriesTag.getConvertedValue());
    }
}
