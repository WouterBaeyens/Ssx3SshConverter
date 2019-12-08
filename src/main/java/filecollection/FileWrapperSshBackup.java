package filecollection;

import java.io.File;

public class FileWrapperSshBackup implements FileWrapper {

    private static final FileExtension FILE_EXTENSION = FileExtension.SSH_BACKUP_EXTENSION;

    private final File sshFileBackup;

    FileWrapperSshBackup(File sshFileBackup) {
        this.sshFileBackup = sshFileBackup;
    }

    @Override
    public File getFile() {
        return sshFileBackup;
    }

    @Override
    public FileExtension getExtension() {
        return FILE_EXTENSION;
    }
}
