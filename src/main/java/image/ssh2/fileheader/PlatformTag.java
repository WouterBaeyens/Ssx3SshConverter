package image.ssh2.fileheader;

import image.ImgSubComponent;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.Arrays;

/**
 * This tag describes the platform the .ssh is targeting.
 * It will most likely be "SHPS" for PS2.
 */
public class PlatformTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;

    public PlatformTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "FileType: " + FileType.getInfo(getBytes());
    }

    public enum FileType {
        PC_FILE("SHPI"),
        PS1_FILE("SHPP"),
        PS2_FILE("SHPS"),
        XBOX_FILE("SHPX"),
        Xbox_FILE("ShpX"),
        PSP_FILE("SHPM");

        final String value;

        FileType(String value) {
            this.value = value;
        }

        public static String getInfo(byte[] data) {
            String dataAsString = new String(data);
            return Arrays.stream(values())
                    .filter(fileType -> fileType.value.equals(dataAsString))
                    .findAny().map(fileType -> fileType + "(" + fileType.value + ")")
                    .orElseGet(() -> "Unknown type (" + dataAsString + ")");
        }
    }
}
