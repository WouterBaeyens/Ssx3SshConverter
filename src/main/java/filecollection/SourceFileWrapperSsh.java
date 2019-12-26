package filecollection;

import java.io.File;

public class SourceFileWrapperSsh implements SourceFileWrapper {

    private static final FileExtension FILE_EXTENSION = FileExtension.SSH_EXTENSION;

    private final File sshFile;

    SourceFileWrapperSsh(File sshFile) {
        this.sshFile = sshFile;
    }

    @Override
    public File getFile() {
        return sshFile;
    }

    @Override
    public FileExtension getExtension() {
        return FILE_EXTENSION;
    }
}
