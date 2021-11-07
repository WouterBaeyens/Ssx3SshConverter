package image.ssh2.attachments;

import image.ImgSubComponent;
import image.ssh2.fileheader.ComponentType;
import image.ssh2.fileheader.TypeComponent;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.function.Function;

public class ImageNameSizeTag extends ImgSubComponent implements TypeComponent<ImageNameSizeTag.ImageType> {

    private static final long DEFAULT_SIZE = 3;

    @Override
    public Class<ImageType> getTypeClass() {
        return ImageType.class;
    }

    public ImageNameSizeTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "?always 0?: " + getTypeInfo();
    }

    public enum ImageType implements ComponentType {
        DEFAULT("000000");

        final String value;

        ImageType(String value) {
            this.value = value;
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
