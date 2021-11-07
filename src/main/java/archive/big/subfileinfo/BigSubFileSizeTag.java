package archive.big.subfileinfo;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

public class BigSubFileSizeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;

    public BigSubFileSizeTag(final ByteBuffer byteBuffer) {
        super(byteBuffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "FileSize: " + ByteUtil.printLongWithHex(getConvertedValue());
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongBE(getRawBytes());
    }

}
