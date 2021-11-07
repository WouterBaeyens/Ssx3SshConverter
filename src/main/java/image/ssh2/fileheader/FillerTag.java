package image.ssh2.fileheader;

import image.ImgSubComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ByteUtil;
import util.PrintUtil;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * It seems this tag is generally filled with 0's, except when the file is compressed.
 * <p>
 * For SSH images:The image data always starts at address 112 + x*128. This buffer fills that gap.
 * Note: this is probably in order to have the image start at 128 (112 + header of 16 bytes), a nice round 0x0100
 * Examples:
 * - when the header is 32 bytes, this buffer will be 80 bytes. (this is often the case)
 * - when the header is 100 bytes, this buffer will be 12 bytes. (this is often the case)
 * - when the header is 112 bytes, this buffer will be 0 bytes.
 * - when the header is 113 bytes, this buffer will be 127 bytes
 * <p>
 */
public class FillerTag extends ImgSubComponent {
    private static Logger LOGGER = LoggerFactory.getLogger(FillerTag.class);

    // This is the position to which this buffer should fill the file with 0's
    public static final long DESIRED_IMG_HEADER_START_ADDRESS = 112;

    public static final byte[] BUY_ERTS_AS_BYTE = {0x42, 0x75, 0x79, 0x20, 0x45, 0x52, 0x54, 0x53};
    public static final byte[] EA_SPORTS_AS_BYTE = {0x45, 0x41, 0x53, 0x70, 0x6F, 0x72, 0x74, 0x73};

    /**
     * If the {@link DEFAULT_START_ADDRESS_INCREMENT} is already surpassed, the next desired start address is
     * {@link DEFAULT_START_ADDRESS_INCREMENT} + a multiple of this value.
     */
    private static final long DEFAULT_START_ADDRESS_INCREMENT = 0x080;

    private final byte[] prefix;

    public FillerTag(final ByteBuffer buffer, final long fillerSize, final byte[] prefix) {
        super(buffer, fillerSize);
        this.prefix = prefix;
        validate();
    }


    @Override
    public String getHexData() {
        if (!startsWithPrefix() || hasNonZeroBytesAfterPrefix() || getSize() < 16) {
            return PrintUtil.toHexString(false, getRawBytes());
        } else {
            return "\"Buy ERTS\"  00 00 00 ........ 00";
        }
    }

    @Override
    public String getInfo() {
        String sizeInfo = "size=" + ByteUtil.printLongWithHex(getSize());

        String contentInfo;
        if (!startsWithPrefix() || hasNonZeroBytesAfterPrefix()) {
            contentInfo = "contains unknown info!!!!";
        } else {
            contentInfo = "all 0's";
        }
        return "FillerTag: " + sizeInfo + "; " + contentInfo;
    }

    private void validate(){
        if(!startsWithPrefix()){
            LOGGER.error("fileBuffer at {} does not start with the expected prefix '{}': '{}'", ByteUtil.printLongWithHex(getStartPos()), prefix, getHexData());
        }
        if(hasNonZeroBytesAfterPrefix()){
            LOGGER.error("fileBuffer at {} has non-zero bytes after the prefix '{}': '{}'", ByteUtil.printLongWithHex(getStartPos()), prefix, getHexData());
        }
    }

    private boolean startsWithPrefix() {
        byte[] hexData = getRawBytes();
        return startsWithPrefix(hexData, prefix);
    }

    private static boolean startsWithPrefix(byte[] hexData, byte[] prefix){
        final int minLength = Math.min(prefix.length, hexData.length);
        for (int i = 0; i < minLength; i++) {
            if (hexData[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    private boolean hasNonZeroBytesAfterPrefix() {
        return IntStream.range(prefix.length, getRawBytes().length).map(length -> getRawBytes()[length])
                .anyMatch(value -> value != 0);
    }

    public static class Reader {
        private byte[] prefix = BUY_ERTS_AS_BYTE;
        private Long desiredStartAddress;
        private Long addressIncrement;
        private Long fillerSize;

        public static Reader fillerTagReader()
        {
            return new Reader();
        }

        public Reader withPrefix(final byte[] prefix){
            this.prefix = prefix;
            return this;
        }

        public Reader withDesiredStartAddress(final long desiredStartAddress){
            this.desiredStartAddress = desiredStartAddress;
            return this;
        }

        public Reader withAddressIncrement(final long addressIncrement){
            this.addressIncrement = addressIncrement;
            return this;
        }

        public Reader withFillerSize(final long fillerSize){
            this.fillerSize = fillerSize;
            return this;
        }

        public FillerTag read(final ByteBuffer buffer){
            final Optional<Long> fillerSize = Optional.ofNullable(this.fillerSize);
            final long increment = Optional.ofNullable(this.addressIncrement).orElse(DEFAULT_START_ADDRESS_INCREMENT);
            final Optional<Long> calculatedSize = Optional.ofNullable(desiredStartAddress)
                    .map(targetAddress -> getNeededFillerSize(buffer.position(), targetAddress, increment));
            if(calculatedSize.isPresent() && fillerSize.isPresent() && !calculatedSize.get().equals(fillerSize.get())){
                LOGGER.error("Calculated padding (={}) differs from defined padding (={})", calculatedSize.get(), fillerSize.get());
            }

            final long neededFillerSize = fillerSize.orElseGet(() ->calculatedSize.get());
            if(!isValid(buffer, (int) neededFillerSize)){
                LOGGER.error("Skipping buffer as filler prefix does not match expected prefix"); // currently only needed for backs.ssh
                return new FillerTag(buffer, 0, prefix);
            }
            return new FillerTag(buffer, neededFillerSize, prefix);
        }

        private boolean isValid(final ByteBuffer buffer, int fillerSize){
            byte[] actualPrefix = new byte[Math.min(fillerSize,prefix.length)];
            buffer.duplicate().get(actualPrefix);
            return startsWithPrefix(actualPrefix, prefix);
        }

        private static long getNeededFillerSize(long currentPosition, final long desiredStartAddress, final long addressIncrement) {
            return Math.floorMod(desiredStartAddress - currentPosition, addressIncrement);
        }
    }
}