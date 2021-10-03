package com.mycompany.sshtobpmconverter;

import converter.BmpImageFileWrapper;
import converter.Image;
import filecollection.FileExtension;
import filecollection.SourceFileWrapperSsh;
import image.ssh2.Ssh2File;
import util.FileUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

import static util.FileUtil.createDir;

public class BmpFileCreator {

    private BmpFileCreator() {
        //utility class, should not be initialized
    }

    public static void create(SourceFileWrapperSsh sshFileWrapper) throws IOException {
        final Path destinationFolder = createDir("output");
        final Path sshFilePath = sshFileWrapper.getFile().toPath();
        final String fileNameWithoutExtension = FileUtil.getNameWithoutExtension(sshFilePath);
        System.out.println("Creating BMP: " + destinationFolder + "/" + fileNameWithoutExtension);

        try(FileChannel fileChannel = FileChannel.open(sshFilePath, EnumSet.of(StandardOpenOption.READ))) {
            final ByteBuffer sshFileBuffer = fileChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
            create(sshFileBuffer, fileNameWithoutExtension, destinationFolder);
        }
        //SshImageFileWrapper sshWrapper = new SshImageFileWrapper(sshDataSource);
        //sshWrapper.printFormatted();
    }

    public static void create(final ByteBuffer sshFileBuffer, final String sshFileNameWithoutExtension, final Path destinationRootFolder) throws IOException {
            Ssh2File ssh2File = new Ssh2File(sshFileBuffer);
            for (Image image : ssh2File.getImages()) {
                BmpImageFileWrapper bmpWrapper = new BmpImageFileWrapper(image);
                bmpWrapper.printFormatted();
                final Path destionationPath;
                if(ssh2File.getImages().size() > 1){
                    destionationPath = FileUtil.createDir(destinationRootFolder.resolve(sshFileNameWithoutExtension))
                            .resolve(sshFileNameWithoutExtension + "." + image.getImageName() + FileExtension.BMP_EXTENSION.value);
                } else {
                    destionationPath = destinationRootFolder.resolve(sshFileNameWithoutExtension + "." + image.getImageName() + FileExtension.BMP_EXTENSION.value);
                }
                writeToFile(bmpWrapper, destionationPath);
        }
    }



    public static void writeToFile(BmpImageFileWrapper bmpWrapper, final Path filePath) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath.toString());
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bmpWrapper.writeToFile(bos);
    }
}
