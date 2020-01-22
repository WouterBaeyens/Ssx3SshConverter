package image.ssh2.imageheader;

import image.ImgSubComponent;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * The purpose and info within this tag are completely unknown.
 * The only thing to go off is that it's value is always 0x02 so far
 * <p>
 * Note: even the name ImageTypeTag is just a wild guess
 */
public class ImageTypeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 1;

    public ImageTypeTag(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "?ImageType?: " + ImageType.getInfo(getBytes());
    }

    public enum ImageType {
        DEFAULT("02");

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
