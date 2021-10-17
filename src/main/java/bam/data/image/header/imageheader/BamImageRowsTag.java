package bam.data.image.header.imageheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

public class BamImageRowsTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 2;

    public BamImageRowsTag(final ByteBuffer buffer){
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "image #rows: " + ByteUtil.printLongWithHex(getConvertedValue());
    }

    public int getConvertedValue(){
        return Math.toIntExact(ByteUtil.convertToLongLE(getBytes()));
    }

}
