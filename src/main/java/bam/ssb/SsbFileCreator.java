package bam.ssb;

import converter.BmpImageFileWrapper;
import util.FileUtil;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SsbFileCreator {

    public static void create(final ByteBuffer ssbBuffer, final String fileNameWithoutExtension, final Path destinationRootFolder) throws IOException {
        final String baseFileName = Paths.get(fileNameWithoutExtension).getFileName().toString();
        int fileNr = 0;
        while (ssbBuffer.hasRemaining()) {
            SsbFile ssbFile = new SsbFile(ssbBuffer);
            final Path destinationFolder = FileUtil.createDir(destinationRootFolder.resolve(fileNameWithoutExtension));
            final String fileName = String.format("%s_%03d.data", baseFileName, fileNr);
            FileUtil.writeToFile(ssbFile.getRawDecompressedSsbData(), fileName, destinationFolder);
            fileNr ++;
        }
    }
}
