package image.ssh2.fileheader;

import image.ImgSubComponent;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * This field always contains "Buy ERTS"...
 * ERTS is what Electronic Arts is called on the stock market.
 * This might be to continuously remind their developers to buy EA stock.
 */
public class EaTag implements ImgSubComponent {

    private static final long DEFAULT_SIZE = 8;
    private final long startPosition;
    private final byte[] data;

    public EaTag(final RandomAccessFile file, final long startPosition) throws IOException {
        this.startPosition = startPosition;
        data = read(file, getStartPos());
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
        return "EA slogan: " + EAType.getInfo(data);
    }

    public enum EAType {
        Buy_ERTS("Buy ERTS");

        final String value;

        EAType(String value) {
            this.value = value;
        }

        public static String getInfo(byte[] data) {
            String dataAsString = new String(data);
            return Arrays.stream(values())
                    .filter(fileType -> fileType.value.equals(dataAsString))
                    .findAny().map(matchingType -> matchingType.value)
                    .orElseGet(() -> "Unknown value (" + dataAsString + ")");
        }
    }
}
