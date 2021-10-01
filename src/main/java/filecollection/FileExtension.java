package filecollection;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;

public enum FileExtension {

    BMP_EXTENSION(".bmp", (path, attr) -> path.toString().endsWith(".bmp")),
    SSH_EXTENSION(".ssh",(path, attr) -> path.toString().endsWith(".ssh") && !path.toString().endsWith("_original.ssh")),
    SSH_BACKUP_EXTENSION("_original.ssh", (path, attr) -> path.toString().endsWith("_original.ssh")),
    BIG_EXTENSION(".big", (path, attr) -> path.toString().endsWith(".big"));

    public final String value;
    public final BiPredicate<Path, BasicFileAttributes> matcher;

    public String getExtension(){
        return value.substring(1);
    }

    FileExtension(final String extension, final BiPredicate<Path, BasicFileAttributes> matcher) {
        this.value = extension;
        this.matcher = matcher;
    }
}
