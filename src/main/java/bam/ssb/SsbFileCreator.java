package bam.ssb;

import bam.data.image.DataFileExtractor;
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
        final Path destinationFolder = FileUtil.createDir(destinationRootFolder.resolve(fileNameWithoutExtension));
        int fileNr = 0;
        while (ssbBuffer.hasRemaining()) {
            SsbFile ssbFile = new SsbFile(ssbBuffer);
            extractImagesAndRemainingToSeparateFolder(ssbFile, destinationFolder, baseFileName, fileNr);
            fileNr ++;
        }
    }

    public static void extractSsbFileRaw(final SsbFile ssbFile, final Path destinationFolder, String baseFileName, int fileNr) throws IOException {
        final String fileName = String.format("%s_%03d.data", baseFileName, fileNr);
        FileUtil.writeToFile(ssbFile.getRawDecompressedSsbData(), fileName, destinationFolder);
    }

    public static void extractImagesAndRemainingToSeparateFolder(final SsbFile ssbFile, final Path destinationFolder, String baseFileName, int fileNr) throws IOException {
        Path subFolder = FileUtil.createDir(destinationFolder.resolve(String.format("%03d",fileNr)));
        DataFileExtractor dataFileExtractor = new DataFileExtractor();
        dataFileExtractor.extractDataFile(ssbFile.getRawDecompressedSsbData(), subFolder);
    }
}
