package image.ssh2.fileheader;

import image.ImgSubComponent;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.stream.IntStream;

/**
 * It seems this tag is generally filled with 0's, except when the file is compressed.
 * <p>
 * The image data always starts at address 112 + x*128. This buffer fills that gap.
 * Examples:
 * - when the header is 32 bytes, this buffer will be 80 bytes. (this is often the case)
 * - when the header is 100 bytes, this buffer will be 12 bytes. (this is often the case)
 * - when the header is 112 bytes, this buffer will be 0 bytes.
 * - when the header is 113 bytes, this buffer will be 127 bytes
 * <p>
 * Looking at retail version files where this buffer is filled with non-zero values (I'm guessing these are compressed image files):
 * hypothesis:  ";" or 0x3B acts as a separator between properties as ";" is often seen here.
 * but what these properties would be and how they are filled is unknown to me.
 */
public class FillerTag extends ImgSubComponent {

    // This is the position to which this buffer should fill the file with 0's
    private static final long DESIRED_START_ADDRESS = 112;

    private static final byte[] BUY_ERTS_AS_BYTE = {0x42, 0x75, 0x79, 0x20, 0x45, 0x52, 0x54, 0x53};

    /**
     * If the {@link DESIRED_START_ADDRESS} is already surpassed, the next desired start address is
     * {@link DESIRED_START_ADDRESS} + a multiple of this value.
     */
    private static final long DESIRED_START_ADDRESS_INCREMENT = 128;

    public FillerTag(final ByteBuffer buffer) {
        this(buffer, buffer.position(), getNeededFillerSize(buffer.position()));
    }

    public FillerTag(final ByteBuffer buffer, final long startPosition, long actualSize) {
        super(buffer, startPosition, actualSize);
    }


    @Override
    public String getHexData() {
        if (!startsWithBuy_ERTS() || hasNonZeroBytesAfterBuy_Erts() || getSize() < 16) {
            return PrintUtil.toHexString(false, getBytes());
        } else {
            return "\"Buy ERTS\"  00 00 00 ........ 00";
        }
    }

    @Override
    public String getInfo() {
        String sizeInfo = "size=" + ByteUtil.printLongWithHex(getSize());

        if (getSize() != getNeededFillerSize()) {
            sizeInfo += "(Not equal to expected size(" + getNeededFillerSize() + ")!!)";
        }
        String contentInfo;
        if (!startsWithBuy_ERTS() || hasNonZeroBytesAfterBuy_Erts()) {
            contentInfo = "contains unknown info!!!!";
        } else {
            contentInfo = "all 0's";
        }
        return "FillerTag: " + sizeInfo + "; " + contentInfo;
    }

    private static long getNeededFillerSize(long currentPosition) {
        return Math.floorMod(DESIRED_START_ADDRESS - currentPosition, DESIRED_START_ADDRESS_INCREMENT);
    }

    private long getNeededFillerSize() {
        return getNeededFillerSize(getStartPos());
    }

    private boolean startsWithBuy_ERTS() {
        byte[] hexData = getBytes();
        final int minLength = Math.min(BUY_ERTS_AS_BYTE.length, hexData.length);
        for (int i = 0; i < minLength; i++) {
            if (hexData[i] != BUY_ERTS_AS_BYTE[i]) {
                return false;
            }
        }
        return true;
    }

    private boolean hasNonZeroBytesAfterBuy_Erts() {
        return IntStream.range(BUY_ERTS_AS_BYTE.length, getBytes().length).map(length -> getBytes()[length])
                .anyMatch(value -> value != 0);
    }
}