package image;

import util.ByteUtil;
import util.PrintUtil;

import java.nio.ByteBuffer;

public class UnknownComponent extends ImgSubComponent{

    public UnknownComponent(final ByteBuffer buffer, final int size, final String expectedValueAsHex){
        this(buffer, size);
        assert expectedValueAsHex.equals(PrintUtil.toHexString(getBytes()));
    }

    public UnknownComponent(final ByteBuffer buffer, final int size){
        super(buffer, size);
    }

    @Override
    public String getInfo() {
        return "Unknown component: " + getHexData();
    }
}
