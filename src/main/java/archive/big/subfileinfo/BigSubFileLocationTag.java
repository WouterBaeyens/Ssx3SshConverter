package archive.big.subfileinfo;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

public class BigSubFileLocationTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;

    public BigSubFileLocationTag(final ByteBuffer byteBuffer) {
        super(byteBuffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return null;
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongBE(getBytes());
    }

}
