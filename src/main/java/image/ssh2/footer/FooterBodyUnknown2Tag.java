package image.ssh2.footer;

import image.ImgSubComponent;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class FooterBodyUnknown2Tag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 8;

    public FooterBodyUnknown2Tag(final ByteBuffer buffer) throws IOException {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "?always 0?: " + ImageType.getInfo(getBytes());
    }

    public enum ImageType {
        DEFAULT("0000000000000000");

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
