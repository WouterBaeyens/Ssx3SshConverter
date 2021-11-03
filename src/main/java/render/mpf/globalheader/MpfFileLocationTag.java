package render.mpf.globalheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

public class MpfFileLocationTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;

    public MpfFileLocationTag(final ByteBuffer byteBuffer) {
        super(byteBuffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "start of meshData: " + ByteUtil.printLongWithHex(getConvertedValue());
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongLE(getBytes());
    }
}
