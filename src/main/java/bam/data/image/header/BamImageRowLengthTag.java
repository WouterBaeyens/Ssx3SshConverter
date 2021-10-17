package bam.data.image.header;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

public class BamImageRowLengthTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 2;

    public BamImageRowLengthTag(final ByteBuffer buffer){
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "image row length: " + ByteUtil.printLongWithHex(getConvertedValue());
    }

    public int getConvertedValue(){
        return Math.toIntExact(ByteUtil.convertToLongLE(getBytes()));
    }

}
