package bam.data.image.header.imageheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

/**
 * Represents the size of the image-pixel-data (as a multiple of 256 or 0x0100 bytes)
 */
public class BamImageSizeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 2;
    private static final int IMAGE_SIZE_MULTIPLE = 256;

    public BamImageSizeTag(final ByteBuffer buffer){
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "image data size: " + ByteUtil.printLongWithHex(getConvertedValue());
    }

    public int getConvertedValue(){
        return Math.toIntExact(ByteUtil.convertToLongLE(getBytes()) * IMAGE_SIZE_MULTIPLE);
    }

}
