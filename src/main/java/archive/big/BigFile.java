package archive.big;

import archive.big.header.BigFileHeader;
import archive.big.header.BigFileHeaderFactory;
import archive.big.header.BigFileTypeTag;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class BigFile {

    final BigFileHeader bigFileHeader;
    final List<BigSubFileInfo> fileInfoList = new ArrayList<>();

    // todo seems to be some kind of indicator for file-types like *.ssh ... more investigation needed
    final List<BigSubFileInfo> strangeFileInfoList = new ArrayList<>();

    public BigFile(final ByteBuffer byteBuffer){
        this.bigFileHeader = BigFileHeaderFactory.readBigFileHeader(byteBuffer);

        final int numberOfEntries = bigFileHeader.getNumberOfEntries();
        BigFileTypeTag.BigArchiveType archiveType = bigFileHeader.getArchiveType();
        for(int entryNr = 0; entryNr < numberOfEntries; entryNr++){
            BigSubFileInfo fileInfo = new BigSubFileInfo(byteBuffer, archiveType);
            if(fileInfo.getSize() == 0){
                strangeFileInfoList.add(fileInfo);
            } else {
                fileInfoList.add(fileInfo);
            }
        }
    }

    public List<BigSubFileInfo> getFileInfoList() {
        return fileInfoList;
    }
}
