package bam.data.image.header.imagetable;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

public class TotalHeaderWithPaddingSizeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 1;

    public TotalHeaderWithPaddingSizeTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "Padding: " + ByteUtil.printLongWithHex(getConvertedValue());
    }

    public int getConvertedValue() {
        return Math.toIntExact(ByteUtil.convertToLongLE(getRawBytes()));
    }
}
