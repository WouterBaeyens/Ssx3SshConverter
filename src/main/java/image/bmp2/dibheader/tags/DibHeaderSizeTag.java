package image.bmp2.dibheader.tags;

import image.ImgSubComponent;
import util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

/**
 * Number of bytes in the DIB header (from this point)
 */
public class DibHeaderSizeTag extends ImgSubComponent {
    private static final long DEFAULT_SIZE = 4;
    private static final Set<DibType> supportedTypes = Set.of(DibType.BITMAPINFOHEADER);

    public DibHeaderSizeTag(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "DibType: " + DibType.getInfo(getConvertedValue());
    }

    public DibType getDibType() {
        return DibType.lookup(getConvertedValue())
                .orElseThrow(() -> new IllegalArgumentException("dib size of " + getConvertedValue() + " is not valid"));
    }

    public long getConvertedValue() {
        return ByteUtil.convertToLongLE(getBytes());
    }

    public enum DibType {

        BITMAPCOREHEADER(12),
        OS22XBITMAPHEADER(64),
        OS22XBITMAPHEADERV2(16),
        BITMAPINFOHEADER(40),
        BITMAPV2INFOHEADER(52),
        BITMAPV3INFOHEADER(56),
        BITMAPV4HEADER(108),
        BITMAPV5HEADER(124);

        final long dibSize;

        DibType(final long dibSize) {
            this.dibSize = dibSize;
        }

        public static Optional<DibType> lookup(long dibSize) {
            return Arrays.stream(values())
                    .filter(fileType -> fileType.dibSize == dibSize)
                    .findAny();
        }

        public static String getInfo(long dibSize) {
            return lookup(dibSize).map(Enum::name)
                    .orElseGet(() -> "Unknown type (" + dibSize + ")");
        }
    }
}
