package image.ssh2.imageheader;

import image.ImgSubComponent;
import image.ssh.*;
import image.ssh2.fileheader.ComponentType;
import image.ssh2.fileheader.TypeComponent;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

/**
 * The purpose of this tag is defining how the image is encoded.
 * <p>
 * Note: the length of this tag is just a wild guess (only the 2nd byte has changed so far).
 * It could be that the tag is only 1 byte long and the other bytes can contain other info
 */
public class ImageEncodingTypeTag extends ImgSubComponent implements TypeComponent<ImageEncodingTypeTag.EncodingType> {

    private static final long DEFAULT_SIZE = 2;

    public ImageEncodingTypeTag(final ByteBuffer sshFileBuffer) {
        super(sshFileBuffer, DEFAULT_SIZE);
    }

    @Override
    public Class<EncodingType> getTypeClass() {
        return EncodingType.class;
    }

    @Override
    public String getInfo() {
        return "EncodingType: " + getTypeInfo();
    }

    /**
     * Get the encoding type used in the image bitmap.
     * (this determines which {@link image.ssh.SshImageDecoderStrategy} should be used.
     *
     * @return the encoding type
     */
    public EncodingType getEncodingType() {
        return getType().orElse(EncodingType.NONE);
    }

    //Take the closest power of 2 rounded down. divide by 4 for a lower rez
    public enum EncodingType implements ComponentType {
        NONE("0000", new NoneDecoderStrategy()),
        INTERLACED("0020", new InterleafedDecoderStrategy()),
        BLOCK_SCRAMBLED("0030", new InterleafedBitwiseDecoderStrategy()),
        EXPERIMENTAL("XXXXXX", new Interleafed2DecoderStrategy());

        final String value;
        private final SshImageDecoderStrategy decoderStrategy;

        EncodingType(String value, SshImageDecoderStrategy decoderStrategy) {
            this.value = value;
            this.decoderStrategy = decoderStrategy;
        }

        public SshImageDecoderStrategy getDecoderStrategy() {
            return decoderStrategy;
        }

        @Override
        public String getReadableValue() {
            return value;
        }

        @Override
        public Function<byte[], String> toReadable() {
            return ByteUtil::bytesToHex;
        }
    }
}
