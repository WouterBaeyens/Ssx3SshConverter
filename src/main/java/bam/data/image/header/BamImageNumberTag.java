package bam.data.image.header;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

public class BamImageNumberTag extends ImgSubComponent {

    /**
     * Guess: image nr
     */
    private static final long DEFAULT_SIZE = 3;

    public BamImageNumberTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        String info = ByteUtil.printLongWithHex(getConvertedValue());
        return "Image nr.: " + info;
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongLE(getRawBytes());
    }
}
