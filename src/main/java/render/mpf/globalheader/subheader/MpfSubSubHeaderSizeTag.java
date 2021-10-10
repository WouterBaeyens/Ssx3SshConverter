package render.mpf.globalheader.subheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

public class MpfSubSubHeaderSizeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;

    public MpfSubSubHeaderSizeTag(final ByteBuffer file) {
        super(file, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "Sub-subHeader size: " + ByteUtil.printLongWithHex(getConvertedValue());
    }


    public long getConvertedValue() {
        return ByteUtil.convertToLongLE(getBytes());
    }

}
