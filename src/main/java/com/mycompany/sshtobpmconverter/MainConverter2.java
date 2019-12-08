package com.mycompany.sshtobpmconverter;

import filecollection.FileExtension;
import filecollection.FileWrapper;
import filecollection.ImageDataFiles;
import filecollection.ImageDataFilesCollector;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class MainConverter2 {

    /**
     * Collect all ssh and bmp files inside the current directory,
     * group them by image name (eg: group image01.ssh; image01_original.ssh and image01.bmp)
     * and create a {@link ImageDataFiles} object for each group
     * Than do 2 things for each {@link ImageDataFiles} object:
     * 1. if no "_original.ssh" file exists yet, create it by copying the normal ".ssh" file.
     * 2. - [if no ".bmp" file exists yet] create ".bmp" from "_original.ssh"
     * - [if a ".bmp" exists already] create ".ssh" from ".bmp" (note: the existing ".ssh" will be overwritten, but the image from the game is still available as "_original.ssh")
     *
     * @param args ignored (it is mandatory to have this parameter for main functions, even if it is not used.)
     */
    public static void main(String[] args) throws IOException {
        final Set<ImageDataFiles> imageDataFilesSet = ImageDataFilesCollector.collectImageDataFiles();
        for (ImageDataFiles imageDataFiles : imageDataFilesSet) {
            if (imageDataFiles.sshBackupFile == null && imageDataFiles.sshFile != null) {
                imageDataFiles.createSshBackupFile();
            }
            if (imageDataFiles.bmpFile == null) {
                createBmpFile(imageDataFiles);
            } else {
                createSshFile(imageDataFiles);
            }
        }
    }

    /**
     * Create a bmp file by converting from {@link ImageDataFiles#sshBackupFile} if it is present,
     * otherwise create the bmp file by converting from {@link ImageDataFiles#sshFile}.
     *
     * @param imageDataFiles the files used as a reference to create the bmp
     */
    private static void createBmpFile(final ImageDataFiles imageDataFiles) throws IOException {
        final FileWrapper referenceSshFile = Optional.<FileWrapper>ofNullable(imageDataFiles.sshBackupFile).orElse(imageDataFiles.sshFile);
        final String bmpFileName = referenceSshFile.getFileNameWithoutExtension() + FileExtension.BMP_EXTENSION.value;
        BmpFileCreator.create(referenceSshFile.getFile(), bmpFileName);
    }

    private static void createSshFile(ImageDataFiles imageDataFiles) {
        // todo call converter
    }
}
