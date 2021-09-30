package filecollection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Used to collect groups of files linked to the same image.
 */
public class ImageDataSourceCollector {

    Map<String, ImageDataSource> imageDataList = new HashMap<>();

    private static final BiPredicate<Path, BasicFileAttributes> NORMAL_SSH_MATCHER = (path, attr) ->
    {
        // System.out.println(path.toString() + " " + (path.toString().endsWith(FileExtension.SSH_EXTENSION.value) && !path.toString().endsWith(FileExtension.SSH_BACKUP_EXTENSION.value)));
        return path.toString().endsWith(FileExtension.SSH_EXTENSION.value) && !path.toString().endsWith(FileExtension.SSH_BACKUP_EXTENSION.value);
    };

    private static final BiPredicate<Path, BasicFileAttributes> SSH_BACKUP_MATCHER = (path, attr) ->
            path.toString().endsWith(FileExtension.SSH_BACKUP_EXTENSION.value);

    private static final BiPredicate<Path, BasicFileAttributes> BMP_MATCHER = (path, attr) ->
            path.toString().endsWith(FileExtension.BMP_EXTENSION.value);

    public static Set<ImageDataSource> collectFilesAndGroupByImage() throws IOException {
        Map<String, ImageDataSource.ImageDataFilesBuilder> imageDataFilesMap = new HashMap<>();

        findFilesInCurrentDirectory(NORMAL_SSH_MATCHER).map(SourceFileWrapperSsh::new)
                .forEach(fileWrapperSsh -> {
                    final String pathWithoutExtension = fileWrapperSsh.getFileNameWithoutExtension();
                    imageDataFilesMap.putIfAbsent(pathWithoutExtension, new ImageDataSource.ImageDataFilesBuilder());
                    imageDataFilesMap.get(pathWithoutExtension).addSshFile(fileWrapperSsh);
                });

        findFilesInCurrentDirectory(BMP_MATCHER).map(SourceFileWrapperBmp::new)
                .forEach(fileWrapperBmp -> {
                    final String pathWithoutExtension = fileWrapperBmp.getFileNameWithoutExtension();
                    imageDataFilesMap.putIfAbsent(pathWithoutExtension, new ImageDataSource.ImageDataFilesBuilder());
                    imageDataFilesMap.get(pathWithoutExtension).addBmpFile(fileWrapperBmp);
                });

        findFilesInCurrentDirectory(SSH_BACKUP_MATCHER).map(SourceFileWrapperSshBackup::new)
                .forEach(fileWrapperSshBackup -> {
                    final String pathWithoutExtension = fileWrapperSshBackup.getFileNameWithoutExtension();
                    imageDataFilesMap.putIfAbsent(pathWithoutExtension, new ImageDataSource.ImageDataFilesBuilder());
                    imageDataFilesMap.get(pathWithoutExtension).addSshBackupFile(fileWrapperSshBackup);
                });

        return imageDataFilesMap.values().stream()
                .map(ImageDataSource.ImageDataFilesBuilder::build)
                .collect(Collectors.toSet());
    }

    private static Stream<File> findFilesInCurrentDirectory(final BiPredicate<Path, BasicFileAttributes> matcher) throws IOException {
        Stream<Path> paths = Files.find(Paths.get("./"), 3, matcher);
        return paths.filter(Files::isRegularFile).map(Path::toFile);
    }
}
