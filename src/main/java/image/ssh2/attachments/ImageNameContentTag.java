package image.ssh2.attachments;

import image.ImgSubComponent;
import util.FileUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Contains the name of the image followed by 0x00's.
 * Often the name is kept at 4 char's, but has at times been longer (ex: 'allergra').
 */
public class ImageNameContentTag extends ImgSubComponent {

    private static final int DEFAULT_SIZE = 12;

    /**
     * In case the content doesn't fit in {@link DEFAULT_SIZE},
     * an additional {@link SIZE_INCREMENT} gets allocated
     */
    private static final int SIZE_INCREMENT = 16;

    private static final byte STOP_BYTE = 0x00;

    public ImageNameContentTag(final ByteBuffer buffer) {
        super(buffer, calculateFieldSize(buffer));
    }


    private static int calculateFieldSize(final ByteBuffer buffer){
        final byte[] content = FileUtil.readUntilStop(buffer.duplicate(), STOP_BYTE);
        final int sizeOfAdditionalAllocationNeeded = Math.floorMod(DEFAULT_SIZE - content.length, SIZE_INCREMENT);
        return content.length + sizeOfAdditionalAllocationNeeded;
    }

    public String getInfo() {
        return "Img:(name=" + getConvertedValue() + ")";
    }

    public String getConvertedValue() {
        return new String(Arrays.copyOf(getRawBytes(), getSize())).replaceAll("\u0000", "");
    }

}
