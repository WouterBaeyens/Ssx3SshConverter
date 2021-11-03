package render.mpf.globalheader;

import image.ImgSubComponent;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class MpfNameTag extends ImgSubComponent {

    private static final int DEFAULT_SIZE = 16;

    public MpfNameTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    public String getInfo() {
        return "Mesh:(name=" + getConvertedValue() + ")";
    }

    public String getConvertedValue() {
        return new String(Arrays.copyOf(getBytes(), getSize())).replaceAll("\u0000", "");
    }

}
