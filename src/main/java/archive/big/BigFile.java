package archive.big;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class BigFile {

    final BigFileHeader bigFileHeader;
    final List<BigSubFileInfo> fileInfoList = new ArrayList<>();

    // todo seems to be some kind of indicator for file-types like *.ssh ... more investigation needed
    final List<BigSubFileInfo> strangeFileInfoList = new ArrayList<>();

    public BigFile(final ByteBuffer byteBuffer){
        this.bigFileHeader = new BigFileHeader(byteBuffer);

        final int numberOfEntries = bigFileHeader.getNumberOfEntries();
        for(int entryNr = 0; entryNr < numberOfEntries; entryNr++){
            BigSubFileInfo fileInfo = new BigSubFileInfo(byteBuffer);
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
