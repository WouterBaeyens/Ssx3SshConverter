package image.bmp2.bmpheader;

import image.ImgSubComponent;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * This tag describes the file type (similar to how the extension .bmp also describes the file type).
 * BM
 * Windows 3.1x, 95, NT, ... etc.
 * BA
 * OS/2 struct bitmap array
 * CI
 * OS/2 struct color icon
 * CP
 * OS/2 const color pointer
 * IC
 * OS/2 struct icon
 * PT
 * OS/2 pointer
 */
public class FileTypeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 2;

    public FileTypeTag(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "FileType: " + FileType.getInfo(getBytes());
    }

    public enum FileType {
        BM("BM");

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
