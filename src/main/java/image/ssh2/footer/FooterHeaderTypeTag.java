package image.ssh2.footer;

import image.ImgSubComponent;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class FooterHeaderTypeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 1;

    public FooterHeaderTypeTag(final ByteBuffer buffer) throws IOException {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "Footer header type: " + FooterHeaderType.getInfo(getBytes());
    }

    public enum FooterHeaderType {
        DEFAULT("i");

        final String value;

        FooterHeaderType(String value) {
            this.value = value;
        }

        public static String getInfo(byte[] data) {
            String dataAsString = new String(data);
            return Arrays.stream(values())
                    .filter(fileType -> fileType.value.equals(dataAsString))
                    .findAny().map(matchingType -> matchingType.value)
                    .orElseGet(() -> "Unknown type (" + dataAsString + ")");
        }
    }
}
