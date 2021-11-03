package render.mpf.globalheader;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;

public class MpfAmountOfSubFiles extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 2;

    public MpfAmountOfSubFiles(final ByteBuffer buffer){
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "#subfiles: "  + ByteUtil.printLongWithHex(getConvertedValue());
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongLE(getBytes());
    }
}
