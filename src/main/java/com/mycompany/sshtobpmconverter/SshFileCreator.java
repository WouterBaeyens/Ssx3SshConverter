package com.mycompany.sshtobpmconverter;

import filecollection.SourceFileWrapperBmp;
import image.bmp2.Bmp2File;

import java.io.File;
import java.io.IOException;

public class SshFileCreator {

    private SshFileCreator() {
        //utility class, should not be initialized
    }

    public static void create(SourceFileWrapperBmp bmpFileWrapper) throws IOException {
        createDir("output_bmp");
        final String sshFilePathBase = "output/" + bmpFileWrapper.getFileNameWithoutExtension();
        SshFileCreator.create(bmpFileWrapper.getFile(), sshFilePathBase);
    }

    private static void createDir(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    private static void create(File bmpDataSource, String filePathBase) throws IOException {
        System.out.println("Creating SSH: " + filePathBase);
        Bmp2File bmp2File = new Bmp2File(bmpDataSource);

    }
}
