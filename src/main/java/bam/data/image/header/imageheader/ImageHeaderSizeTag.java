package bam.data.image.header.imageheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

public class ImageHeaderSizeTag extends ImgSubComponent {

    /**
     * One byte is suspicious, so it might not refer to the header size after all
     */
    private static final long DEFAULT_SIZE = 1;

    public ImageHeaderSizeTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        String info = ByteUtil.printLongWithHex(getConvertedValue());
        return "Header Size: " + info;
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongLE(getRawBytes());
    }
}