package image.ssh2.fileheader;

import image.ImgSubComponent;

import java.nio.ByteBuffer;
import java.util.function.Function;

/**
 * This tag describes the platform the .ssh is targeting.
 * It will most likely be "SHPS" for PS2.
 */
public class PlatformTag extends ImgSubComponent implements TypeComponent<PlatformTag.FileType> {

    private static final long DEFAULT_SIZE = 4;

    @Override
    public Class<FileType> getTypeClass() {
        return FileType.class;
    }

    public PlatformTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "FileType: " + getTypeInfo();
    }

    public enum FileType implements ComponentType {
        PC_FILE("SHPI"),
        PS1_FILE("SHPP"),
        PS2_FILE("SHPS"),
        XBOX_FILE("SHPX"),
        Xbox_FILE("ShpX"),
        PSP_FILE("SHPM");

        final String value;

        FileType(String value) {
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
