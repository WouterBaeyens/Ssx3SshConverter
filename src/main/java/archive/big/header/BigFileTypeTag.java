package archive.big.header;

import image.ImgSubComponent;
import image.ssh2.fileheader.ComponentType;
import image.ssh2.fileheader.FileTypeTag;
import image.ssh2.fileheader.TypeComponent;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.function.Function;

public class BigFileTypeTag extends ImgSubComponent implements TypeComponent<BigFileTypeTag.VersionType> {

    private static final long DEFAULT_SIZE = 4;

    public BigFileTypeTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "archive type: " + getTypeInfo();
    }

    @Override
    public Class<VersionType> getTypeClass() {
        return VersionType.class;
    }

    public enum VersionType implements ComponentType {
        BIG("BIGF"),
        TRICKY_BIG("BIG4");

        final String value;

        VersionType(String value) {
            this.value = value;
        }

        @Override
        public String getReadableValue() {
            return value;
        }

        @Override
        public Function<byte[], String> toReadable() {
            return String::new;
        }
    }

}
