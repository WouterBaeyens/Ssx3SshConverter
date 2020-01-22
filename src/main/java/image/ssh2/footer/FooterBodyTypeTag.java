package image.ssh2.footer;

import image.ImgSubComponent;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class FooterBodyTypeTag extends ImgSubComponent {
    private static final long DEFAULT_SIZE = 1;

    public FooterBodyTypeTag(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "Footer info type: " + FooterHeaderType.getInfo(getBytes());
    }

    public enum FooterHeaderType {
        DEFAULT("p");

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
