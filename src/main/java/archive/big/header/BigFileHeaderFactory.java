package archive.big.header;

import java.nio.ByteBuffer;

public class BigFileHeaderFactory {

    public static BigFileHeader readBigFileHeader(final ByteBuffer buffer){
        BigFileTypeTag.BigArchiveType archiveType = new BigFileTypeTag(buffer.duplicate()).getType().orElse(BigFileTypeTag.BigArchiveType.SSX3_BIG);
        return switch (archiveType){
            case SSX3_BIG -> new SSX3BigFileHeader(buffer);
            case TRICKY_BIG ->  new SSX3BigFileHeader(buffer);
            case REF_PACK_ARCHIVE -> new RefPackBigFileHeader(buffer);
        };
    }
}
