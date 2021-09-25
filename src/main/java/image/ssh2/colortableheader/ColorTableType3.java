package image.ssh2.colortableheader;

import image.ImgSubComponent;
import util.PrintUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * The purpose and info within this tag are completely unknown.
 * The only thing to go off is that it's value is always 0x00 00 00 00 20 00 00 so far
 *
 * <p>
 * Note: even the name TableType3Tag is just a wild guess
 */
public class ColorTableType3 extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 6;

    public ColorTableType3(final ByteBuffer buffer) throws IOException {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "?TableType3?: " + ImageType.getInfo(getBytes());
    }

    public enum ImageType {
        /**
         * This type could refer to the amount of bits per pixel (4), or the way to interpret the info in the table
         * So far I have only found this value in "crwd.ssh" (crowd textures) - where ImageTypeTag is also unique
         */
        LOW_RES("000000000000"),

        /**
         */
        DEFAULT("000000200000");

        final String value;

        ImageType(final String value) {
            this.value = value;
        }

        public static String getInfo(byte[] data) {
            String dataAsString = PrintUtil.toHexString(false, data).trim().replace(" ", "");
            return Arrays.stream(values())
                    .filter(fileType -> fileType.value.equals(dataAsString))
                    .findAny().map(matchingType -> matchingType + "(" + matchingType.value + ")")
                    .orElseGet(() -> "Unknown type (" + dataAsString + ")");
        }
    }
}
