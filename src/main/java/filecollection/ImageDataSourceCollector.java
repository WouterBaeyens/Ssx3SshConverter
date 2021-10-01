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

import static util.FileUtil.findFilesInCurrentDirectory;

/**
 * Used to collect groups of files linked to the same image.
 */
public class ImageDataSourceCollector {

    Map<String, ImageDataSource> imageDataList = new HashMap<>();

    public static Set<ImageDataSource> collectFilesAndGroupByImage() throws IOException {
        Map<String, ImageDataSource.ImageDataFilesBuilder> imageDataFilesMap = new HashMap<>();

        findFilesInCurrentDirectory(FileExtension.SSH_EXTENSION).map(SourceFileWrapperSsh::new)
                .forEach(fileWrapperSsh -> {
                    final String pathWithoutExtension = fileWrapperSsh.getFileNameWithoutExtension();
                    imageDataFilesMap.putIfAbsent(pathWithoutExtension, new ImageDataSource.ImageDataFilesBuilder());
                    imageDataFilesMap.get(pathWithoutExtension).addSshFile(fileWrapperSsh);
                });

        findFilesInCurrentDirectory(FileExtension.BMP_EXTENSION).map(SourceFileWrapperBmp::new)
                .forEach(fileWrapperBmp -> {
                    final String pathWithoutExtension = fileWrapperBmp.getFileNameWithoutExtension();
                    imageDataFilesMap.putIfAbsent(pathWithoutExtension, new ImageDataSource.ImageDataFilesBuilder());
                    imageDataFilesMap.get(pathWithoutExtension).addBmpFile(fileWrapperBmp);
                });

        findFilesInCurrentDirectory(FileExtension.SSH_BACKUP_EXTENSION).map(SourceFileWrapperSshBackup::new)
                .forEach(fileWrapperSshBackup -> {
                    final String pathWithoutExtension = fileWrapperSshBackup.getFileNameWithoutExtension();
                    imageDataFilesMap.putIfAbsent(pathWithoutExtension, new ImageDataSource.ImageDataFilesBuilder());
                    imageDataFilesMap.get(pathWithoutExtension).addSshBackupFile(fileWrapperSshBackup);
                });

        return imageDataFilesMap.values().stream()
                .map(ImageDataSource.ImageDataFilesBuilder::build)
                .collect(Collectors.toSet());
    }

}
