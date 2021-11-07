package bam.ssb;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

public class SsbComponentSizeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 3;

    public SsbComponentSizeTag(final ByteBuffer buffer){
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "file size: " + ByteUtil.printLongWithHex(getConvertedValue());
    }

    public int getConvertedValue(){
        return Math.toIntExact(ByteUtil.convertToLongBE(getRawBytes()));
    }
}
