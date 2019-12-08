package com.mycompany.sshtobpmconverter;

import converter.BmpImageFileWrapper;
import image.ssh2.Ssh2File;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BmpFileCreator {

    public static void create(File sshFile, String filePath) throws IOException {
        System.out.println("Creating BMP: " + filePath);
        Ssh2File ssh2File = new Ssh2File(sshFile);
        ssh2File.close();
        //SshImageFileWrapper sshWrapper = new SshImageFileWrapper(sshFile);
        //sshWrapper.printFormatted();
//
//        BmpImageFileWrapper bmpWrapper = new BmpImageFileWrapper(sshWrapper);
//        bmpWrapper.printFormatted();

        // writeToFile(bmpWrapper, filePath);
    }

    public static void writeToFile(BmpImageFileWrapper bmpWrapper, String filePath) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bmpWrapper.writeToFile(bos);
    }
}
