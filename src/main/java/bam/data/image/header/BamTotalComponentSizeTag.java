package bam.data.image.header;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

public class BamTotalComponentSizeTag extends ImgSubComponent {

    /**
     * Total size of the component - does not include the 8 byte header of which this component is a part
     */
    private static final long DEFAULT_SIZE = 3;

    public BamTotalComponentSizeTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        String info = ByteUtil.printLongWithHex(getConvertedValue());
        return "TotalFileSize: " + info;
    }

    public int getConvertedValue() {
        return Math.toIntExact(ByteUtil.convertToLongLE(getRawBytes()));
    }
}
