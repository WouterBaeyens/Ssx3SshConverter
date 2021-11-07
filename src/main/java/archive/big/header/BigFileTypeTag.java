package archive.big.header;

import image.ImgSubComponent;
import image.ssh2.fileheader.ComponentType;
import image.ssh2.fileheader.TypeComponent;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;

public class BigFileTypeTag extends ImgSubComponent implements TypeComponent<BigFileTypeTag.BigArchiveType> {
    // added for use in the enum below
    static final byte[] REF_PACK_BINARY = parseHexBinary("C0FB");

    private static final int DEFAULT_SIZE = 4;

    public BigFileTypeTag(final ByteBuffer buffer) {
        super(buffer, determineSize(buffer));
    }

    private static int determineSize(final ByteBuffer buffer){
        byte[] bytes = new byte[2];
        buffer.duplicate().get(bytes);
        if(BigArchiveType.isRefPack(bytes)){
            return BigArchiveType.REF_PACK_ARCHIVE.getReadableValue().length();
        } else {
            return DEFAULT_SIZE;
        }
    }

    @Override
    public String getInfo() {
        return "archive type: " + getTypeInfo();
    }

    @Override
    public Optional<BigArchiveType> getType() {
        return TypeComponent.super.getType();
    }

    @Override
    public Class<BigArchiveType> getTypeClass() {
        return BigArchiveType.class;
    }

    public enum BigArchiveType implements ComponentType {
        SSX3_BIG("BIGF"),
        TRICKY_BIG("BIG4"),
        REF_PACK_ARCHIVE(new String(REF_PACK_BINARY));

        final String value;

        BigArchiveType(String value) {
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

        public static boolean isRefPack(byte[] bytes){
            return Arrays.equals(Arrays.copyOf(bytes, 2), REF_PACK_BINARY);
        }
    }

}
