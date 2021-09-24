package image.ssh2.imageheader;

import image.ImgSubComponent;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * The purpose and info within this tag is completely unknown.
 * Always all 0's so far
 * <p>
 * Note: even the name ImageMaterialTag is just a wild guess
 **/
public class ImageMaterialTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;

    public ImageMaterialTag(final ByteBuffer sshFileBuffer) throws IOException {
        super(sshFileBuffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "?Material?: " + MaterialType.getInfo(getBytes());
    }

    public enum MaterialType {
        DEFAULT("00000000");

        final String value;

        MaterialType(String value) {
            this.value = value;
        }

        public static String getInfo(byte[] data) {
            //todo check if Hex.encodeHexString(data); is sufficient
            String dataAsString = PrintUtil.toHexString(false, data).trim().replace(" ", "");

            return Arrays.stream(values())
                    .filter(fileType -> fileType.value.equals(dataAsString))
                    .findAny().map(matchingType -> matchingType + "(" + matchingType.value + ")")
                    .orElseGet(() -> "Unknown Material (" + dataAsString + ")");
        }
    }
}
