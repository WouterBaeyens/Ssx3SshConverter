package image.ssh2.colortableheader;

import image.ImgSubComponent;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * This tag describes the type of table.
 * So far it looks to always be 0x21
 */
public class ColorTableTypeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 1;

    public ColorTableTypeTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "TableType: " + ImageType.getInfo(getBytes());
    }

    public enum ImageType {
        DEFAULT("21");

        final String value;

        ImageType(String value) {
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
