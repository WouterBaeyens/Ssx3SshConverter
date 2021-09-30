package filecollection;

import java.io.File;

public class SourceFileWrapperSshBackup extends SourceFileWrapperSsh {

    private static final FileExtension FILE_EXTENSION = FileExtension.SSH_BACKUP_EXTENSION;

    SourceFileWrapperSshBackup(File sshFileBackup) {
        super(sshFileBackup);
    }

    @Override
    public FileExtension getExtension() {
        return FILE_EXTENSION;
    }
}
