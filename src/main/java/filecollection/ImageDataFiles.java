package filecollection;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageDataFiles {

    // Contains the original file as it was in the game.
    public final FileWrapperSsh sshFile;

    // Contains the bmp file with the image from the sshFile
    public final FileWrapperBmp bmpFile;

    // Contains the original ssh file and keeps it as a backup,
    // since the purpose of this program is to modify the sshFile based on modifications of the bmp file.
    public final FileWrapperSshBackup sshBackupFile;

    public void createSshBackupFile() throws IOException {
        Path backupPath = new File(sshFile.getFileNameWithoutExtension() + FileExtension.SSH_BACKUP_EXTENSION.value).toPath();
        Files.copy(sshFile.getFile().toPath(), backupPath);
    }

    private ImageDataFiles(ImageDataFilesBuilder imageDataFilesBuilder) {
        sshFile = imageDataFilesBuilder.sshFile;
        bmpFile = imageDataFilesBuilder.bmpFile;
        sshBackupFile = imageDataFilesBuilder.sshBackupFile;
    }

    static class ImageDataFilesBuilder {

        private FileWrapperSsh sshFile;
        private FileWrapperBmp bmpFile;
        private FileWrapperSshBackup sshBackupFile;

        ImageDataFilesBuilder addSshFile(final FileWrapperSsh sshFile) {
            this.sshFile = sshFile;
            return this;
        }

        ImageDataFilesBuilder addBmpFile(final FileWrapperBmp bmpFile) {
            this.bmpFile = bmpFile;
            return this;
        }

        ImageDataFilesBuilder addSshBackupFile(final FileWrapperSshBackup sshBackupFile) {
            this.sshBackupFile = sshBackupFile;
            return this;
        }

        ImageDataFiles build() {
            return new ImageDataFiles(this);
        }
    }
}
