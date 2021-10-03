package archive.big.subfileinfo;

import image.ImgSubComponent;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class BigSubFileNameTag extends ImgSubComponent {

    private static final byte STOP_BYTE = 0x00;

    public BigSubFileNameTag(final ByteBuffer byteBuffer){
        super(byteBuffer, STOP_BYTE);
    }

    @Override
    public String getInfo() {
        return "filename: \"" + getConvertedValue() + "\"";
    }

    public String getConvertedValue() {
        return new String(Arrays.copyOf(getBytes(), getBytes().length - 1));
    }
}
