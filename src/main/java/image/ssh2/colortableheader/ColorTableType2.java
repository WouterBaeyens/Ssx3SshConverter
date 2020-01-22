package image.ssh2.colortableheader;

import image.ImgSubComponent;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * The purpose and info within this tag are completely unknown.
 * The only thing to go off is that it's value is always 0x01 so far
 *
 * The header is always 0x10 bytes so far, so maybe related if read differently?
 * <p>
 * Note: even the name TableType2Tag is just a wild guess
 */
public class ColorTableType2 extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 2;

    public ColorTableType2(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "?TableType2?: " + ImageType.getInfo(getBytes());
    }

    public enum ImageType {
        DEFAULT("0100");

        final String value;

        ImageType(String value) {
            this.value = value;
        }

        public static String getInfo(byte[] data) {
            String dataAsString = PrintUtil.toHexString(false, data).trim().replace(" ", "");
            return Arrays.stream(values())
                    .filter(fileType -> fileType.value.equals(dataAsString))
                    .findAny().map(matchingType -> matchingType.toString() + "(" + matchingType.value + ")")
                    .orElseGet(() -> "Unknown type (" + dataAsString + ")");
        }
    }
}
