package image.ssh2.fileheader;

import image.ImgSubComponent;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * (uncertain)
 * This tag seems to describe the version of .ssh being used. (eg. G357)
 * It can give an idea about the relative age of this file.
 */
public class VersionTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;

    public VersionTag(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
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
                    .findAny().map(matchingType -> matchingType.toString() + "(" + matchingType.value + ")")
                    .orElseGet(() -> "Unknown (" + dataAsString + ")");
        }
    }
}
