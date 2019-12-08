package image.ssh2.fileheader;

import image.ImgSubComponent;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * This tag describes the file type (similar to how the extension .ssh also describes the file type).
 * It will most likely be "SHPS", describing the file as an ssh, also known as a compressed .fsh (which would have filetype "SHPI")
 */
public class FileTypeTag implements ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;
    private final long startPosition;
    private final byte[] data;

    public FileTypeTag(final RandomAccessFile file, final long startPosition) throws IOException {
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
        return "FileType: " + FileType.getInfo(data);
    }

    public enum FileType {
        SHPS("SHPS");

        final String value;

        FileType(String value) {
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
