package archive.big;

import bam.data.image.BamImageComponent;
import bam.ssb.SsbFile;
import bam.ssb.SsbFileCreator;
import com.google.common.io.Files;
import com.mycompany.sshtobpmconverter.BmpFileCreator;
import filecollection.FileExtension;
import image.ssh2.compression.CompressedFile;
import image.ssh2.fileheader.PlatformTag;
import render.mpf.MpfFileExtractor;
import util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

import static util.FileUtil.writeToFile;

public class BigFileExtractor {

    public void extractBigFile(final File file) throws IOException {
        assertFileType(file);
        final Path filePath = file.toPath();
        try(FileChannel fileChannel = FileChannel.open(filePath, EnumSet.of(StandardOpenOption.READ))){
            final MappedByteBuffer bigFilebuffer = fileChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
            extractBigFile(bigFilebuffer, filePath);
        }
    }

    private void extractBigFile(final ByteBuffer bigFileBuffer, final Path filePath) throws IOException {
        BigFile bigFile = new BigFile(bigFileBuffer);
        final Path bigFolder = filePath.resolveSibling(FileUtil.getNameWithoutExtension(filePath));
        FileUtil.createDir(bigFolder);
        for(BigSubFileInfo fileInfo : bigFile.getFileInfoList()){
            extractFile(bigFileBuffer, fileInfo, bigFolder);
        }
    }

    private void extractFile(final ByteBuffer bigFileBuffer, final BigSubFileInfo fileInfo, final Path destinationFolder) throws IOException {
        final ByteBuffer fileBuffer = FileUtil.slice(bigFileBuffer, Math.toIntExact(fileInfo.getLocation()), fileInfo.getSize());
        final ByteBuffer decompressedFileBuffer = new CompressedFile(fileBuffer).decompress();
        if(FileExtension.SSH_EXTENSION.getExtension().equals(fileInfo.getExtension())) {
            BmpFileCreator.create(decompressedFileBuffer, fileInfo.getName(), destinationFolder);
        } else if(FileExtension.BIG_EXTENSION.getExtension().equals(fileInfo.getExtension())){
            extractBigFile(decompressedFileBuffer, destinationFolder.resolve(fileInfo.getName()));
        } else if(FileExtension.MPF_EXTENSION.getExtension().equals(fileInfo.getExtension())){
            writeToFile(new MpfFileExtractor(decompressedFileBuffer).getMergedBuffer(), fileInfo.getFullName(), destinationFolder);
        } else if(FileExtension.SSB_EXTENSION.getExtension().equals(fileInfo.getExtension())){
            SsbFileCreator.create(fileBuffer, fileInfo.getName(), destinationFolder);
        } else {
            writeToFile(decompressedFileBuffer, fileInfo.getFullName(), destinationFolder);
        }
    }

    private void assertFileType(File bigFile) {
        if (!isBigFile(bigFile)) {
            throw new IllegalArgumentException("only bmp files allowed!");
        }
    }

    private boolean isBigFile(File file) {
        String extension = Files.getFileExtension(file.getPath());
        return extension.equals(FileExtension.BIG_EXTENSION.getExtension());
    }
}
