package image.ssh2.attachments;

import image.ImgSubComponent;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ImageNameSizeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 3;

    public ImageNameSizeTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "?always 0?: " + ImageType.getInfo(getBytes());
    }

    public enum ImageType {
        DEFAULT("000000");

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
