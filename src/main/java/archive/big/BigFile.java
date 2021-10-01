package archive.big;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class BigFile {

    final BigFileHeader bigFileHeader;
    final List<BigSubFileInfo> fileInfoList = new ArrayList<>();

    public BigFile(final ByteBuffer byteBuffer){
        this.bigFileHeader = new BigFileHeader(byteBuffer);

        final int numberOfEntries = bigFileHeader.getNumberOfEntries();
        for(int entryNr = 0; entryNr < numberOfEntries; entryNr++){
            fileInfoList.add(new BigSubFileInfo(byteBuffer));
        }
    }

    public List<BigSubFileInfo> getFileInfoList() {
        return fileInfoList;
    }

    public void extractContents(){

    }
}
