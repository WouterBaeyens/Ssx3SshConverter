package image.ssh2.fileheader;

import image.ImgSubComponent;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.stream.IntStream;

/**
 * No much is known about this tag.
 * It seems it is generally filled with 0's, except when the file is compressed.
 * My guess is that ";" or 0x3B acts as a separator between it's properties.
 * but what these properties are and how they are filled is unknown to me.
 */
public class CompressionTag implements ImgSubComponent {

    /**
     * arielle_icons: 15 images, size= 96 bytes
     * moby_bord_a01: 1 image, size= 80 bytes
     * hypothesis: increases per image? (bitmap?)
     */
    private static final int DEFAULT_SIZE = 80;
    private final int actualTagSize;
    private final long startPosition;
    private final byte[] data;

    public CompressionTag(final RandomAccessFile file, final long startPosition) throws IOException {
        this(file, startPosition, DEFAULT_SIZE);
    }

    public CompressionTag(final RandomAccessFile file, final long startPosition, long size) throws IOException {
        this.startPosition = startPosition;
        data = read(file, getStartPos());
        actualTagSize = (int) size;
    }

    @Override
    public long getSize() {
        return actualTagSize;
    }

    @Override
    public long getStartPos() {
        return startPosition;
    }

    @Override
    public String getHexData() {
        if (hasNonZeroBytes()) {
            return PrintUtil.toHexString(false, data);
        } else {
            return "00 00 00 00 ........ 00";
        }
    }

    @Override
    public String getInfo() {
        String sizeInfo = "size=" + getSize() + "/0x" + Long.toHexString(getSize());
        if (getSize() != DEFAULT_SIZE) {
            sizeInfo += "(Not equal to expected size(" + DEFAULT_SIZE + ")!!)";
        }
        String contentInfo;
        if (hasNonZeroBytes()) {
            contentInfo = "contains unknown info!!!!";
        } else {
            contentInfo = "all 0's";
        }
        return "CompressionTag: " + sizeInfo + "; " + contentInfo;
    }

    private boolean hasNonZeroBytes() {
        long amountOfNonZeroBytes = IntStream.range(0, data.length).map(i -> data[i])
                .filter(value -> value != 0)
                .count();
        return amountOfNonZeroBytes > 0;
    }
}