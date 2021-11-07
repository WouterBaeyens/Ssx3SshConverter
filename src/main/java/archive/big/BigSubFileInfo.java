package archive.big;

import archive.big.header.BigFileTypeTag;
import archive.big.subfileinfo.BigSubFileLocationTag;
import archive.big.subfileinfo.BigSubFileNameTag;
import archive.big.subfileinfo.BigSubFileSizeTag;
import com.google.common.io.Files;
import image.AbstractAmountTag;
import image.AmountTag;
import util.ByteUtil;
import util.FileUtil;

import java.nio.ByteBuffer;

public class BigSubFileInfo {

    private final AmountTag.Reader sizeTagReader = new AmountTag.Reader().withName("Sub-File size");
    private final AmountTag.Reader locationTagReader = new AmountTag.Reader().withName("Start of sub-file");

    /**
     * Contains start address of the subfile relative to the BIG file
     */
    private final AmountTag locationTag;
    private final AmountTag sizeTag;
    private final BigSubFileNameTag nameTag;

    public BigSubFileInfo(final ByteBuffer byteBuffer, BigFileTypeTag.BigArchiveType archiveType){
        switch (archiveType){
            case SSX3_BIG, TRICKY_BIG -> {
                this.locationTag = locationTagReader.withSize(4).read(byteBuffer);
                this.sizeTag = sizeTagReader.withSize(4).read(byteBuffer);
                this.nameTag = new BigSubFileNameTag(byteBuffer);
            }
            case REF_PACK_ARCHIVE -> {
                this.locationTag = locationTagReader.withSize(3).read(byteBuffer);
                this.sizeTag = sizeTagReader.withSize(3).read(byteBuffer);
                this.nameTag = new BigSubFileNameTag(byteBuffer);

            }
            default -> {throw new IllegalStateException("Unsupported archive type: " + archiveType);}
        }
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
