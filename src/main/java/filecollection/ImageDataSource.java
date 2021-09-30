package filecollection;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageDataSource {

    // Contains the original file as it was in the game.
    public final SourceFileWrapperSsh sshFile;

    // Contains the bmp file with the image from the sshFile
    public final SourceFileWrapperBmp bmpFile;

    // Contains the original ssh file and keeps it as a backup,
    // since the purpose of this program is to modify the sshFile based on modifications of the bmp file.
    public final SourceFileWrapperSshBackup sshBackupFile;

    public void createSshBackupFile() throws IOException {
        Path backupPath = new File(sshFile.getFileNameWithoutExtension() + FileExtension.SSH_BACKUP_EXTENSION.value).toPath();
        Files.copy(sshFile.getFile().toPath(), backupPath);
    }

    private ImageDataSource(ImageDataFilesBuilder imageDataFilesBuilder) {
        sshFile = imageDataFilesBuilder.sshFile;
        bmpFile = imageDataFilesBuilder.bmpFile;
        sshBackupFile = imageDataFilesBuilder.sshBackupFile;
    }

    static class ImageDataFilesBuilder {

        private SourceFileWrapperSsh sshFile;
        private SourceFileWrapperBmp bmpFile;
        private SourceFileWrapperSshBackup sshBackupFile;

        ImageDataFilesBuilder addSshFile(final SourceFileWrapperSsh sshFile) {
            this.sshFile = sshFile;
            return this;
        }

        ImageDataFilesBuilder addBmpFile(final SourceFileWrapperBmp bmpFile) {
            this.bmpFile = bmpFile;
            return this;
        }

        ImageDataFilesBuilder addSshBackupFile(final SourceFileWrapperSshBackup sshBackupFile) {
            this.sshBackupFile = sshBackupFile;
            return this;
        }

        ImageDataSource build() {
            return new ImageDataSource(this);
        }
    }
}
