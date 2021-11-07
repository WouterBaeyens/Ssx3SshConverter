package image.ssh2.compression;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

public class DecompressedFileSizeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 3;

    public DecompressedFileSizeTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "FileSize: " + ByteUtil.printLongWithHex(getConvertedValue());
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongBE(getRawBytes());
    }

}
