package com.mycompany.sshtobpmconverter;

import filecollection.ImageDataSource;
import filecollection.ImageDataSourceCollector;
import filecollection.SourceFileWrapperBmp;
import filecollection.SourceFileWrapperSsh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.Set;
import java.nio.MappedByteBuffer;

public class MainConverter2 {

    /**
     * Collect all ssh and bmp files inside the current directory,
     * group them by image name (eg: group image01.ssh; image01_original.ssh and image01.bmp)
     * and create a {@link ImageDataSource} object for each group
     * Than do 2 things for each {@link ImageDataSource} object:
     * 1. if no "_original.ssh" file exists yet, create it by copying the normal ".ssh" file.
     * 2. - [if no ".bmp" file exists yet] create ".bmp" from "_original.ssh"
     * - [if a ".bmp" exists already] create ".ssh" from ".bmp" (note: the existing ".ssh" will be overwritten, but the image from the game is still available as "_original.ssh")
     *
     * @param args ignored (it is mandatory to have this parameter for main functions, even if it is not used.)
     */
    public static void main(String[] args) throws IOException {
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
