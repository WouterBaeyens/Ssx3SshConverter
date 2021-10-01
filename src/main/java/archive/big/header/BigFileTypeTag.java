package archive.big.header;

import image.ImgSubComponent;
import image.ssh2.fileheader.FileTypeTag;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class BigFileTypeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;

    public BigFileTypeTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "archive type: " + VersionType.getInfo(getBytes());
    }

    public enum VersionType {
        BIG("BIGF");

        final String value;

        VersionType(String value) {
            this.value = value;
        }

        public static String getInfo(byte[] data) {
            String dataAsString = new String(data);
            return Arrays.stream(values())
                    .filter(fileType -> fileType.value.equals(dataAsString))
                    .findAny().map(matchingType -> matchingType + "(" + matchingType.value + ")")
                    .orElseGet(() -> "Unknown (" + dataAsString + ")");
        }
    }

}
