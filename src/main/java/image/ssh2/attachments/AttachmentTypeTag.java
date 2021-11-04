package image.ssh2.attachments;

import image.ImgSubComponent;
import image.ssh2.fileheader.ComponentType;
import image.ssh2.fileheader.TypeComponent;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class AttachmentTypeTag extends ImgSubComponent implements TypeComponent<AttachmentTypeTag.AttachmentType> {
    private static final long DEFAULT_SIZE = 1;

    @Override
    public Class<AttachmentType> getTypeClass() {
        return AttachmentType.class;
    }

    public AttachmentTypeTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "Attachment type: " + getTypeInfo();
    }

    public enum AttachmentType implements ComponentType {
        METAL_BIN("69"), //
        IMAGE_NAME("70"),
        HOT_SPOT("7C"); // only encountered in fe_1.ssh, not much is known about this attachment

        final String value;

        AttachmentType(String value) {
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
