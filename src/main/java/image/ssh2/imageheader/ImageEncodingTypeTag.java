package image.ssh2.imageheader;

import image.ImgSubComponent;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Optional;

/**
 * The purpose of this tag is defining how the image is encoded.
 * <p>
 * Note: the length of this tag is just a wild guess (only the 2nd byte has changed so far).
 * It could be that the tag is only 1 byte long and the other bytes can contain other info
 */
public class ImageEncodingTypeTag implements ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;
    private final long startPosition;
    private final byte[] data;

    public ImageEncodingTypeTag(final RandomAccessFile file, final long startPosition) throws IOException {
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
        return "EncodingType: " + EncodingType.getInfo(data);
    }

    /**
     * Get the encoding type used in the image bitmap.
     * (this determines which {@link image.ssh.SshImageDecoderStrategy} should be used.
     *
     * @return the encoding type
     */
    public EncodingType getEncodingType() {
        return EncodingType.getEncodingType(data)
                .orElse(EncodingType.NONE);
    }

    public enum EncodingType {
        NONE("00000000"),
        INTERLEAFED("00200000");

        final String value;

        EncodingType(String value) {
            this.value = value;
        }

        public static Optional<EncodingType> getEncodingType(byte[] data) {
            String dataAsString = PrintUtil.toHexString(false, data).trim().replaceAll(" ", "");
            ;
            return Arrays.stream(values())
                    .filter(fileType -> fileType.value.equals(dataAsString))
                    .findAny();
        }

        public static String getInfo(byte[] data) {
            String dataAsString = PrintUtil.toHexString(false, data).trim().replaceAll(" ", "");
            ;
            return getEncodingType(data)
                    .map(matchingType -> matchingType.toString() + "(" + matchingType.value + ")")
                    .orElseGet(() -> "Unknown encoding (" + dataAsString + ")");
        }
    }
}
