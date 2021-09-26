package image.ssh2.attachments;

import image.ImgSubComponent;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Supposedly this is the content of metalBin and helps with image processing: https://fifam.miraheze.org/wiki/FSH#Section:_Metal_bin
 * Until more is known about the content (and the content is not always the same)
 * I'll group everything together in 1 tag.
 */
public class MetalBinContentTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 12;

    public MetalBinContentTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "?Metal Bin content?: " + ImageType.getInfo(getBytes());
    }

    public enum ImageType {
        DEFAULT("000080000000000000000000");

        final String value;

        ImageType(String value) {
            this.value = value;
        }

        public static String getInfo(byte[] data) {
            String dataAsString = ByteUtil.bytesToHex(data);

            return Arrays.stream(values())
                    .filter(fileType -> fileType.value.equals(dataAsString))
                    .findAny().map(matchingType -> matchingType + "(" + matchingType.value + ")")
                    .orElseGet(() -> "Unknown type (" + dataAsString + ")");
        }
    }
}
