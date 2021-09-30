/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.sshtobpmconverter;

import converter.BmpImageFileWrapper;
import converter.SshImageFileWrapper;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

//import java.util.stream.Stream;

/**
 * @author Wouter
 */
public class MainConverter {
    static final String BMP_EXT = ".bmp";
    static final String SSH_EXT = ".ssh";
    static final String SSH_ORIG_EXT = "_original.ssh";

    public static void mainOld(String[] args) throws FileNotFoundException, IOException, DecoderException {

        //basically a lot of if's to do the following:
        //convert bmp (if present) to ssh (keep the original ssh by renaming it to _original.ssh
        //otherwise convert ssh to bmp (converting _original.ssh gets president over converting .ssh)

        //a lot of if-else and hardcoding extensions etc is not the best way, but it's how i get it done quick
        Optional<Path> bmpPath = findFile(BMP_EXT);
        Optional<Path> sshPath = findFile(SSH_EXT);
        Optional<Path> originalSshPath = findFile(SSH_ORIG_EXT);


        File bmpFile;
        File sshFile = null;
        //if no bmp is present, convert ssh to bmp
        if (!bmpPath.isPresent()) {
            System.out.println("Converting ssh to bmp");
            String bmpFileName = "";
            //if the _orignal.ssh is present, use this one for the conversion
            if (originalSshPath.isPresent()) {
                sshFile = originalSshPath.get().toFile();
                String sshFileName = sshFile.getName();
                bmpFileName = replaceEnding(sshFileName, SSH_ORIG_EXT, BMP_EXT);
                //else use the normal .ssh for converting
            } else if (sshPath.isPresent()) {
                sshFile = sshPath.get().toFile();
                String sshFileName = sshFile.getName();
                bmpFileName = replaceEnding(sshFileName, SSH_EXT, BMP_EXT);
            } else {
                throw new IOException("No ssh file to convert from found");
            }
            //actual conversion from ssh to bmp
            System.out.println("BMP: " + bmpFileName);
            FileOutputStream fos = new FileOutputStream(bmpFileName);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            SshImageFileWrapper sshWrapper = new SshImageFileWrapper(sshFile);
            sshWrapper.printFormatted();
            BmpImageFileWrapper bmpWrapper = new BmpImageFileWrapper(sshWrapper);
            bmpWrapper.printFormatted();
            bmpWrapper.writeToFile(bos);
        }
        //convert bmp to ssh
        else {
            System.out.println("converting bmp to ssh");
            bmpFile = bmpPath.get().toFile();
            String sshOutputFileName = "";
            //rename .ssh to _original.ssh in order not to overwrite it
            if (!originalSshPath.isPresent()) {
                if (sshPath.isPresent()) {
                    sshOutputFileName = sshPath.get().getFileName().toString();
                    sshFile = sshPath.get().toFile();
                    String originalSshFileName = replaceEnding(sshOutputFileName, SSH_EXT, SSH_ORIG_EXT);
                    File originalSshFile = new File(originalSshFileName);
                    boolean success = sshFile.renameTo(originalSshFile);
                    sshFile = originalSshFile;
                    if (!success)
                        throw new IOException("renaming ssh file to _original failed");
                    //if no original ssh is present, do a standard conversion
                } else {
                    sshOutputFileName = replaceEnding(bmpFile.getName(), BMP_EXT, SSH_EXT);
                }
            } else {
                sshFile = originalSshPath.get().toFile();
                sshOutputFileName = replaceEnding(sshFile.getName(), SSH_ORIG_EXT, SSH_EXT);
            }
            System.out.println("SSH: " + sshOutputFileName);
            FileOutputStream fos = new FileOutputStream(sshOutputFileName);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            BmpImageFileWrapper bmpWrapper = new BmpImageFileWrapper(bmpFile);
            bmpWrapper.printFormatted();
            SshImageFileWrapper sshWrapper = new SshImageFileWrapper(bmpWrapper);
            //check if the original ssh is available to copy metaData from
            if (sshFile != null) {
                SshImageFileWrapper originalSshWrapper = new SshImageFileWrapper(sshFile);
                sshWrapper.updateMetaData(originalSshWrapper.getMetaData());
            }
            sshWrapper.printFormatted();
            sshWrapper.writeToFile(bos);

        }
    }

    public static String getExtention(File file) {
        String fileName = file.getName();
        int index = StringUtils.lastIndexOf(fileName, ".");
        if (index == -1) {
            throw new RuntimeException("There isn't an extension to determine the type of file");
        } else {
            return fileName.substring(index + 1);
        }
    }

    public static Optional<Path> findFile(String extension) throws IOException {
        BiPredicate<Path, BasicFileAttributes> matcher = (path, attr) -> {
            return path.toString().endsWith(extension);
        };
        try (Stream<Path> paths = Files.find(Paths.get("./"), 1, matcher)) {
            return paths.filter(Files::isRegularFile).findAny();
        }
    }

    /**
     * This means finding the file that ends with .ssh, but not _original.ssh.
     *
     * @return
     * @throws java.io.IOException
     */
    public static Optional<Path> findNonOriginalSshFile() throws IOException {
        BiPredicate<Path, BasicFileAttributes> matcher = (path, attr) -> {
            return path.toString().endsWith(".ssh") && !path.toString().endsWith("_original.ssh");
        };
        try (Stream<Path> paths = Files.find(Paths.get("./"), 1, matcher)) {
            return paths.filter(Files::isRegularFile).findAny();
        }
    }

    public static String replaceEnding(String fullString, String from, String to) {
        return fullString.substring(0, fullString.lastIndexOf(from)) + to;
    }

    public static String removeExtensionFromFileName(String fileName) {
        String[] fileNameParts = fileName.split(".");
        String extension = fileNameParts[fileNameParts.length - 1];
        return fileName.substring(0, (fileName.length() - extension.length()) - 1);
    }
}
