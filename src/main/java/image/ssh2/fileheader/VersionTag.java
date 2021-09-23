package image.ssh2.fileheader;

import image.ImgSubComponent;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.util.Arrays;

/**
 * (uncertain)
 * This tag seems to describe the version of .ssh being used. (eg. G357)
 * It can give an idea about the relative age of this file.
 */
public class VersionTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;

    public VersionTag(final MappedByteBuffer buffer) throws IOException {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "version: " + VersionType.getInfo(getBytes());
    }

    public enum VersionType {
        SSX3("G357"),
        SSX3_ALPHA("G352");

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
