package filecollection;

import java.io.File;

public class SourceFileWrapperBmp implements SourceFileWrapper {

    private static final FileExtension FILE_EXTENSION = FileExtension.BMP_EXTENSION;

    private final File bmpFile;

    SourceFileWrapperBmp(File bmpFile) {
        this.bmpFile = bmpFile;
    }

    @Override
    public File getFile() {
        return bmpFile;
    }

    @Override
    public FileExtension getExtension() {
        return FILE_EXTENSION;
    }
}
