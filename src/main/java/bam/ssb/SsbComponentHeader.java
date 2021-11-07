package bam.ssb;

import image.UnknownComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

public class SsbComponentHeader {

    final SsbComponentTypeTag ssbComponentTypeTag;
    final SsbComponentSizeTag fileSizeTag;
    final UnknownComponent padding;

    public SsbComponentHeader(final ByteBuffer byteBuffer){
        this.ssbComponentTypeTag = new SsbComponentTypeTag(byteBuffer);
        this.fileSizeTag = new SsbComponentSizeTag(byteBuffer);
        this.padding = new UnknownComponent(byteBuffer, 1);
    }

    public int getSubFileSize(){
        return fileSizeTag.getConvertedValue();
    }

    public boolean isLastComponentOfFile(){
        return ssbComponentTypeTag.getComponentType() == SsbComponentTypeTag.SsbComponentType.CEND;
    }

    public int getSize(){
        return ssbComponentTypeTag.getSize() + fileSizeTag.getSize() + padding.getSize();
    }

    public byte[] getBytes(){
        return ByteUtil.combineByteArrays(ssbComponentTypeTag.getRawBytes(), fileSizeTag.getRawBytes());
    }

    @Override
    public String toString() {
        return "SsbHeader{" +
                "offset=" + ByteUtil.printLongWithHex(ssbComponentTypeTag.getStartPos()) +
                ", ssbFileTypeTag=" + ssbComponentTypeTag +
                ", fileSizeTag=" + fileSizeTag +
                '}';
    }
}
