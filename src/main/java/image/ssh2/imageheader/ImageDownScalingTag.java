package image.ssh2.imageheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

/**
 * Describes the amount of downscaled images contained in the image data.
 * Each subsequent image has 1/4th the resolution of the previous one.
 *
 * 00 10 -> no downscaling included in image data
 * 00 10 -> 1 level of downscaling included in image data
 * 00 20 -> 2 levels of downscaling included in image data
 * 00 30 -> 3 levels of downscaling included in image data
 */
public class ImageDownScalingTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 2;

    public ImageDownScalingTag(ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
        assertRightNibbleEmpty();
    }

    public void assertRightNibbleEmpty(){
        if(getRawValue() % 0x01000 != 0 || getConvertedValue() > 3){
            throw new IllegalStateException("Expected downscaling value [0-3], but was " + ByteUtil.bytesToHex(getRawBytes()));
        }
    }

    @Override
    public String getInfo() {
        return "#downscales: " + getConvertedValue();
    }

    public int getConvertedValue(){
        return Math.toIntExact(ByteUtil.convertToLongLE(getRawBytes()) / 0x01000);
    }

    private int getRawValue(){
        return Math.toIntExact(ByteUtil.convertToLongLE(getRawBytes()));
    };
}
