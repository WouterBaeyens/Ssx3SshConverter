package filecollection;

public enum FileExtension {

    BMP_EXTENSION(".bmp"),
    SSH_EXTENSION(".ssh"),
    SSH_BACKUP_EXTENSION("_original.ssh");

    public final String value;

    private FileExtension(final String extension) {
        this.value = extension;
    }
}
