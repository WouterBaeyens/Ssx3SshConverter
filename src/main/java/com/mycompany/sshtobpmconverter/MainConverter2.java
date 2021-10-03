package com.mycompany.sshtobpmconverter;

import archive.big.BigFileExtractor;
import filecollection.*;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import static util.FileUtil.findFilesInCurrentDirectory;

public class MainConverter2 {

    public static void main(String[] args) throws IOException {
        extractBigFiles();
        convertSshFiles();
    }

    public static void extractBigFiles() throws IOException {
        findFilesInCurrentDirectory(FileExtension.BIG_EXTENSION)
                .forEach(MainConverter2::extractBigFile);
    }

    public static void extractBigFile(final File file) throws IllegalStateException {
        BigFileExtractor extractor = new BigFileExtractor();
        try {
            extractor.extractBigFile(file);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }


        //todo consider launch4j to create an exec
    /**
     * Collect all ssh and bmp files inside the current directory,
     * group them by image name (eg: group image01.ssh; image01_original.ssh and image01.bmp)
     * and create a {@link ImageDataSource} object for each group
     * Than do 2 things for each {@link ImageDataSource} object:
     * 1. if no "_original.ssh" file exists yet, create it by copying the normal ".ssh" file.
     * 2. - [if no ".bmp" file exists yet] create ".bmp" from "_original.ssh"
     * - [if a ".bmp" exists already] create ".ssh" from ".bmp" (note: the existing ".ssh" will be overwritten, but the image from the game is still available as "_original.ssh")
     *
     */
    public static void convertSshFiles() throws IOException {
        // todo use ByteBuffer for in-between and mappedByteBuffer to combo with RandomAccess
        final Set<ImageDataSource> imageDataSourceSet = ImageDataSourceCollector.collectFilesAndGroupByImage();
        for (ImageDataSource imageDataSource : imageDataSourceSet) {
//            if (imageDataSource.sshBackupFile == null && imageDataSource.sshFile != null) {
//                imageDataSource.createSshBackupFile();
//            }
            if (imageDataSource.bmpFile == null) {
                createBmpFile(imageDataSource);
            }
//            if (imageDataSource.bmpFile != null) {
//                createSshFile(imageDataSource);
//            }
        }
    }

    /**
     * Create a bmp file by converting from {@link ImageDataSource#sshBackupFile} if it is present,
     * otherwise create the bmp file by converting from {@link ImageDataSource#sshFile}.
     *
     * @param imageDataSource the image data used as a reference to create the bmp file
     */
    private static void createBmpFile(final ImageDataSource imageDataSource) throws IOException {
        final SourceFileWrapperSsh referenceSshFile = Optional.<SourceFileWrapperSsh>ofNullable(imageDataSource.sshBackupFile).orElse(imageDataSource.sshFile);
        BmpFileCreator.create(referenceSshFile);
    }

    private static void createSshFile(ImageDataSource imageDataSource) throws IOException {
        final SourceFileWrapperBmp referenceBmpFile = imageDataSource.bmpFile;
        SshFileCreator.create(referenceBmpFile);
    }
}
