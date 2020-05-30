package com.mycompany.sshtobpmconverter;

import converter.BmpImageFileWrapper;
import filecollection.SourceFileWrapperSsh;
import image.ssh2.Ssh2File;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BmpFileCreator {

    private BmpFileCreator() {
        //utility class, should not be initialized
    }

    public static void create(SourceFileWrapperSsh sshFileWrapper) throws IOException {
        createDir("output");
        final String bmpFilePathBase = "output/" + sshFileWrapper.getFileNameWithoutExtension();
        BmpFileCreator.create(sshFileWrapper.getFile(), bmpFilePathBase);

    }

    private static void createDir(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    private static void create(File sshDataSource, String filePathBase) throws IOException {
        System.out.println("Creating BMP: " + filePathBase);
        Ssh2File ssh2File = new Ssh2File(sshDataSource);
//        for (Image image : ssh2File.getImages()) {
//            BmpImageFileWrapper bmpWrapper = new BmpImageFileWrapper(image);
//            bmpWrapper.printFormatted();
//            String fullFilePath = filePathBase + "." + image.getImageName() + FileExtension.BMP_EXTENSION.value;
//            writeToFile(bmpWrapper, fullFilePath);
//        }

        ssh2File.close();

        //SshImageFileWrapper sshWrapper = new SshImageFileWrapper(sshDataSource);
        //sshWrapper.printFormatted();
//

    }

    public static void writeToFile(BmpImageFileWrapper bmpWrapper, String filePath) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bmpWrapper.writeToFile(bos);
    }
}
