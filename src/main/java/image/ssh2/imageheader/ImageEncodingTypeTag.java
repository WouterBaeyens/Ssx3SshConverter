package image.ssh2.imageheader;

import image.ImgSubComponent;
import image.ssh.InterleafedDecoderStrategy;
import image.ssh.NoneDecoderStrategy;
import image.ssh.SshImageDecoderStrategy;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;

/**
 * The purpose of this tag is defining how the image is encoded.
 * <p>
 * Note: the length of this tag is just a wild guess (only the 2nd byte has changed so far).
 * It could be that the tag is only 1 byte long and the other bytes can contain other info
 */
public class ImageEncodingTypeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;

    public ImageEncodingTypeTag(final ByteBuffer sshFileBuffer) throws IOException {
        super(sshFileBuffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "EncodingType: " + EncodingType.getInfo(getBytes());
    }

    /**
     * Get the encoding type used in the image bitmap.
     * (this determines which {@link image.ssh.SshImageDecoderStrategy} should be used.
     *
     * @return the encoding type
     */
    public EncodingType getEncodingType() {
        return EncodingType.getEncodingType(getBytes())
                .orElse(EncodingType.NONE);
    }

    public enum EncodingType {
        NONE("00000000", new NoneDecoderStrategy()),
        INTERLACED("00200000", new InterleafedDecoderStrategy());

        final String value;
        private final SshImageDecoderStrategy decoderStrategy;

        EncodingType(String value, SshImageDecoderStrategy decoderStrategy) {
            this.value = value;
            this.decoderStrategy = decoderStrategy;
        }

        public SshImageDecoderStrategy getDecoderStrategy() {
            return decoderStrategy;
        }

        public static Optional<EncodingType> getEncodingType(byte[] data) {
            String dataAsString = PrintUtil.toHexString(false, data).trim().replace(" ", "");
            return Arrays.stream(values())
                    .filter(fileType -> fileType.value.equals(dataAsString))
                    .findAny();
        }

        public static String getInfo(byte[] data) {
            String dataAsString = PrintUtil.toHexString(false, data).trim().replace(" ", "");
            return getEncodingType(data)
                    .map(matchingType -> matchingType + "(" + matchingType.value + ")")
                    .orElseGet(() -> "Unknown encoding (" + dataAsString + ")");
        }
    }
}
