package image.ssh2.attachments;

import image.ImgSubComponent;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class AttachmentTypeTag extends ImgSubComponent {
    private static final long DEFAULT_SIZE = 1;

    public AttachmentTypeTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "Attachment type: " + AttachmentType.getInfo(getBytes());
    }

    public enum AttachmentType {
        METAL_BIN("69"), //
        IMAGE_NAME("70");

        final String value;

        AttachmentType(String value) {
            this.value = value;
        }

        public static AttachmentType getAttachmentType(final byte data){
            String dataAsString = ByteUtil.bytesToHex(new byte[]{data});
            return Arrays.stream(values())
                    .filter(attachmentType -> attachmentType.value.equals(dataAsString))
                    .findAny()
                    .orElseThrow(() -> new NoSuchElementException("Unsupported attachment-type tag: \""+ dataAsString + "\""));
        }

        public static String getInfo(final byte[] data) {
            String dataAsString = ByteUtil.bytesToHex(data);
            return Arrays.stream(values())
                    .filter(attachmentType -> attachmentType.value.equals(dataAsString))
                    .findAny().map(matchingType -> matchingType + "(" + matchingType.value + ")")
                    .orElseGet(() -> "Unknown type (" + dataAsString + ")");
        }
    }
}
