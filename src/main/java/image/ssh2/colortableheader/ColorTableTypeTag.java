package image.ssh2.colortableheader;

import image.ImgSubComponent;
import image.ssh2.fileheader.ComponentType;
import image.ssh2.fileheader.TypeComponent;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.function.Function;

/**
 * This tag describes the type of table.
 * So far it looks to always be 0x21
 */
public class ColorTableTypeTag extends ImgSubComponent implements TypeComponent<ColorTableTypeTag.ImageType> {

    private static final long DEFAULT_SIZE = 1;

    public ColorTableTypeTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "TableType: " + getTypeInfo();
    }

    @Override
    public Class<ImageType> getTypeClass() {
        return ImageType.class;
    }

    public enum ImageType implements ComponentType {
        DEFAULT("21");

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
