package image.ssh2.imageheader;

import image.ImgSubComponent;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * The purpose and info within this tag is completely unknown.
 * Always all 0's so far
 * <p>
 * Note: even the name ImageMaterialTag is just a wild guess
 **/
public class ImageMaterialTag implements ImgSubComponent {
    private static final long DEFAULT_SIZE = 4;
    private final long startPosition;
    private final byte[] data;

    public ImageMaterialTag(final RandomAccessFile file, final long startPosition) throws IOException {
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
        return "?Material?: " + MaterialType.getInfo(data);
    }

    public enum MaterialType {
        DEFAULT("00000000");

        final String value;

        MaterialType(String value) {
            this.value = value;
        }

        public static String getInfo(byte[] data) {
            String dataAsString = PrintUtil.toHexString(false, data).trim().replaceAll(" ", "");
            ;
            return Arrays.stream(values())
                    .filter(fileType -> fileType.value.equals(dataAsString))
                    .findAny().map(matchingType -> matchingType.toString() + "(" + matchingType.value + ")")
                    .orElseGet(() -> "Unknown Material (" + dataAsString + ")");
        }
    }
}
