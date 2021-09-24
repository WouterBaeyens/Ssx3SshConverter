package image.ssh2.footer;

import image.ImgSubComponent;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Until more is known about the content (and the content is not always the same)
 * I'll group everything together in 1 tag.
 */
public class FooterHeaderUnknown1Tag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 12;

    public FooterHeaderUnknown1Tag(final ByteBuffer buffer) throws IOException {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "?Footer header stuff?: " + ImageType.getInfo(getBytes());
    }

    public enum ImageType {
        DEFAULT("000080000000000000000000");

        final String value;

        ImageType(String value) {
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
