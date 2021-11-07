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

/**
 * Supposedly this is the content of metalBin and helps with image processing: https://fifam.miraheze.org/wiki/FSH#Section:_Metal_bin
 * Until more is known about the content (and the content is not always the same)
 * I'll group everything together in 1 tag.
 */
public class MetalBinContentTag extends ImgSubComponent implements TypeComponent<MetalBinContentTag.ImageType> {

    private static final long DEFAULT_SIZE = 12;

    @Override
    public Class<ImageType> getTypeClass() {
        return ImageType.class;
    }

    public MetalBinContentTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "?Metal Bin content?: " + getTypeInfo();
    }


    public enum ImageType implements ComponentType {
        DEFAULT("000080000000000000000000");

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
