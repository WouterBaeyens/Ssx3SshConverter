package image.ssh2.fileheader;

import image.ImgSubComponent;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.Arrays;
import java.util.function.Function;

/**
 * (uncertain)
 * This tag describes the type of .ssh being used. (eg. G357)
 * It can give an idea about the relative age of this file.
 */
public class FileTypeTag extends ImgSubComponent implements TypeComponent<FileTypeTag.VersionType>{

    private static final long DEFAULT_SIZE = 4;

    @Override
    public Class<VersionType> getTypeClass() {
        return VersionType.class;
    }

    public FileTypeTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "dir version: " + getTypeInfo();
    }

    public enum VersionType implements ComponentType{
        TRICKY_PRE_ALPHA("G247"), // attachments. see specmap.ssh
        SSX3_PRE_ALPHA1("G264"), // table size is not defined in header, no attachments
        SSX3_PRE_ALPHA2("G268"), // table size is not defined in header, no attachments
        SSX_TRICKY("G278"), // SSX_TRICKY, no attachments
        SSX3_ALPHA("G352"),
        SSX3("G357");

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
