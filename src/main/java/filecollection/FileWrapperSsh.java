package filecollection;

import java.io.File;

public class FileWrapperSsh implements FileWrapper {

    private static final FileExtension FILE_EXTENSION = FileExtension.SSH_EXTENSION;

    private final File sshFile;

    FileWrapperSsh(File sshFile) {
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
