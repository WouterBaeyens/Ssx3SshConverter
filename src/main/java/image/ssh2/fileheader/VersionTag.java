package image.ssh2.fileheader;

import image.ImgSubComponent;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * (uncertain)
 * This tag seems to describe the version of .ssh being used. (eg. G357)
 * It can give an idea about the relative age of this file.
 */
public class VersionTag implements ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;
    private final long startPosition;
    private final byte[] data;

    public VersionTag(final RandomAccessFile file, final long startPosition) throws IOException {
        this.startPosition = startPosition;
        data = read(file, startPosition);
    }

    @Override
    public long getSize() {
        return DEFAULT_SIZE;
    }

    @Override
    public long getStartPos() {
        return startPosition;
    }

    @Override
    public String getHexData() {
        return PrintUtil.toHexString(false, data);
    }

    @Override
    public String getInfo() {
        return "version: " + VersionType.getInfo(data);
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
