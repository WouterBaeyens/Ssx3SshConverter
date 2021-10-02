package image.ssh2.attachments;

import image.ImgSubComponent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Contains the name of the image followed by 0x00's.
 * Often the name is kept at 4 char's, but has at times been longer (ex: 'allergra').
 */
public class ImageNameContentTag extends ImgSubComponent {

    private static final int DEFAULT_SIZE = 12;

    public ImageNameContentTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    public String getInfo() {
        return "Img:(name=" + getConvertedValue() + ")";
    }

    public String getConvertedValue() {
        return new String(Arrays.copyOf(getBytes(), DEFAULT_SIZE)).replaceAll("\u0000", "");
    }

}
