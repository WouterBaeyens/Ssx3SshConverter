package image.ssh2.imageheader;

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
 * The purpose and info within this tag is completely unknown.
 * Always all 0's so far
 * <p>
 * Note: even the name ImageMaterialTag is just a wild guess
 **/
public class ImageMaterialTag extends ImgSubComponent implements TypeComponent<ImageMaterialTag.MaterialType> {

    private static final long DEFAULT_SIZE = 4;

    @Override
    public Class<MaterialType> getTypeClass() {
        return MaterialType.class;
    }

    public ImageMaterialTag(final ByteBuffer sshFileBuffer) {
        super(sshFileBuffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "?Material?: " + getTypeInfo();
    }


    public enum MaterialType implements ComponentType {
        DEFAULT("00000000");

        final String value;

        MaterialType(String value) {
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
