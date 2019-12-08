package image.ssh2.colortableheader;

import image.ImgSubComponent;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * The purpose and info within this tag are completely unknown.
 * The only thing to go off is that it's value is always 0x02 so far
 * <p>
 * Note: even the name ImageTypeTag is just a wild guess
 */
public class ColorTableTypeTag implements ImgSubComponent {

    private static final long DEFAULT_SIZE = 1;
    private final long startPosition;
    private final byte[] data;

    public ColorTableTypeTag(final RandomAccessFile file, final long startPosition) throws IOException {
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
        return "?TableType?: " + ImageType.getInfo(data);
    }

    public enum ImageType {
        DEFAULT("21");

        final String value;

        ImageType(String value) {
            this.value = value;
        }

        public static String getInfo(byte[] data) {
            String dataAsString = PrintUtil.toHexString(false, data).trim().replaceAll(" ", "");
            ;
            return Arrays.stream(values())
                    .filter(fileType -> fileType.value.equals(dataAsString))
                    .findAny().map(matchingType -> matchingType.toString() + "(" + matchingType.value + ")")
                    .orElseGet(() -> "Unknown type (" + dataAsString + ")");
        }
    }
}
