package bam.data.image.header.imageheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

public class BamImageHeightTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 2;

    public BamImageHeightTag(final ByteBuffer buffer){
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "image height (high_rez, in pixels): " + ByteUtil.printLongWithHex(getConvertedValue());
    }

    public int getConvertedValue(){
        return Math.toIntExact(ByteUtil.convertToLongLE(getBytes()));
    }

}
