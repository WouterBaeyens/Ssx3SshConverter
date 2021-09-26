package image.ssh2.colortableheader;

import image.ImgSubComponent;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * The purpose and info within this tag are completely unknown.
 *
 * <p>
 * Note: even the name TableType2Tag is just a wild guess
 */
public class ColorTableType2 extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 6;

    public ColorTableType2(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "?TableType2?: " + ImageType.getInfo(getBytes());
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
            String dataAsString = ByteUtil.bytesToHex(data);
            return Arrays.stream(values())
                    .filter(fileType -> fileType.value.equals(dataAsString))
                    .findAny().map(matchingType -> matchingType + "(" + matchingType.value + ")")
                    .orElseGet(() -> "Unknown type (" + dataAsString + ")");
        }
    }
}
