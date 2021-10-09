package image.ssh2.fileheader;

import image.ImgSubComponent;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.Arrays;

/**
 * (uncertain)
 * This tag describes the type of .ssh being used. (eg. G357)
 * It can give an idea about the relative age of this file.
 */
public class FileTypeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;

    public FileTypeTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "dir version: " + VersionType.getInfo(getBytes());
    }

    public enum VersionType {
        SSX3_PRE_ALPHA1("G264"), // table size is not defined in header, no attachments
        SSX3_PRE_ALPHA2("G268"), // table size is not defined in header, no attachments
        SSX_TRICKY("G278"), // SSX_TRICKY, no attachments
        SSX3_ALPHA("G352"),
        SSX3("G357");

        final String value;

        VersionType(String value) {
            this.value = value;
        }

        public static String getInfo(byte[] data) {
            String dataAsString = new String(data);
            return Arrays.stream(values())
                    .filter(fileType -> fileType.value.equals(dataAsString))
                    .findAny().map(matchingType -> matchingType + "(" + matchingType.value + ")")
                    .orElseGet(() -> "Unknown (" + dataAsString + ")");
        }
    }
}
