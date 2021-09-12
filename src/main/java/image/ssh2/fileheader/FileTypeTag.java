package image.ssh2.fileheader;

import image.ImgSubComponent;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * This tag describes the file type (similar to how the extension .ssh also describes the file type).
 * It will most likely be "SHPS", describing the file as an ssh, also known as a compressed .fsh (which would have filetype "SHPI")
 */
public class FileTypeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;

    public FileTypeTag(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "FileType: " + FileType.getInfo(getBytes());
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