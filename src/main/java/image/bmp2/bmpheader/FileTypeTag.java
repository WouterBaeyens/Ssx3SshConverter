package image.bmp2.bmpheader;

import image.ImgSubComponent;
import image.ssh2.fileheader.ComponentType;
import image.ssh2.fileheader.TypeComponent;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.function.Function;

/**
 * This tag describes the file type (similar to how the extension .bmp also describes the file type).
 * BM
 * Windows 3.1x, 95, NT, ... etc.
 * BA
 * OS/2 struct bitmap array
 * CI
 * OS/2 struct color icon
 * CP
 * OS/2 const color pointer
 * IC
 * OS/2 struct icon
 * PT
 * OS/2 pointer
 */
public class FileTypeTag extends ImgSubComponent implements TypeComponent<FileTypeTag.FileType> {

    private static final long DEFAULT_SIZE = 2;

    @Override
    public Class<FileType> getTypeClass() {
        return FileType.class;
    }

    public FileTypeTag(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "FileType: " + getTypeInfo();
    }

    public enum FileType implements ComponentType {
        BM("BM");

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
