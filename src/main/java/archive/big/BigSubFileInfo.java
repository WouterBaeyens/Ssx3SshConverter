package archive.big;

import archive.big.subfileinfo.BigSubFileLocationTag;
import archive.big.subfileinfo.BigSubFileNameTag;
import archive.big.subfileinfo.BigSubFileSizeTag;
import com.google.common.io.Files;
import util.ByteUtil;
import util.FileUtil;

import java.nio.ByteBuffer;

public class BigSubFileInfo {

    private final BigSubFileLocationTag locationTag;
    private final BigSubFileSizeTag sizeTag;
    private final BigSubFileNameTag nameTag;

    public BigSubFileInfo(final ByteBuffer byteBuffer){
        this.locationTag = new BigSubFileLocationTag(byteBuffer);
        this.sizeTag = new BigSubFileSizeTag(byteBuffer);
        this.nameTag = new BigSubFileNameTag(byteBuffer);
    }

    public String getInfo() {
        return "File:(name=" + getFullName() + "; location=" + ByteUtil.printLongWithHex(getLocation()) + "; size=" + getSize() + ")";
    }

    public String getName(){
        return FileUtil.getNameWithoutExtension(nameTag.getConvertedValue());
    }

    public String getFullName(){
        return nameTag.getConvertedValue();
    }

    public String getExtension(){
        return Files.getFileExtension(getFullName());
    }

    public long getLocation(){
        return locationTag.getConvertedValue();
    }

    public int getSize(){
        return Math.toIntExact(sizeTag.getConvertedValue());
    }

    @Override
    public String toString() {
        return getInfo();
    }
}
