package bam.data.image;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

/**
 * This component describes the padding.
 * It contains 2 bytes for the lenght, followed by 0's
 */
public class BamPadding {

    private final BamPaddingSizeTag paddingSizeTag;

    public BamPadding(final ByteBuffer buffer, final int bytesPreceeding){
        this.paddingSizeTag = new BamPaddingSizeTag(buffer);

    }

    public class BamPaddingSizeTag extends ImgSubComponent {

        public static int DEFAULT_SIZE = 2;

        public BamPaddingSizeTag(ByteBuffer buffer) {
            super(buffer, DEFAULT_SIZE);
        }

        @Override
        public String getInfo() {
            return "Header + padding size = " + getConvertedValue();
        }

        public int getConvertedValue(){
            return Math.toIntExact(ByteUtil.convertToLongLE(getRawBytes()));
        }
    }
}
