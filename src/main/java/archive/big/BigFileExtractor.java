package archive.big;

import com.google.common.io.Files;
import com.mycompany.sshtobpmconverter.BmpFileCreator;
import filecollection.FileExtension;
import image.ssh2.compression.CompressedFile;
import image.ssh2.fileheader.PlatformTag;
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

public class BigFileExtractor {

    public void extractBigFile(final File file) throws IOException {
        assertFileType(file);
        final Path filePath = file.toPath();
        try(FileChannel fileChannel = FileChannel.open(filePath, EnumSet.of(StandardOpenOption.READ))){
            final MappedByteBuffer bigFilebuffer = fileChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
            BigFile bigFile = new BigFile(bigFilebuffer);
            final Path bigFolder = filePath.resolveSibling(FileUtil.getNameWithoutExtension(filePath));
            FileUtil.createDir(bigFolder);
            for(BigSubFileInfo fileInfo : bigFile.getFileInfoList()){
                extractFile(bigFilebuffer, fileInfo, bigFolder);
            }
        }
    }

    private void extractFile(final ByteBuffer bigFilebuffer, final BigSubFileInfo fileInfo, final Path destinationFolder) throws IOException {
        final ByteBuffer fileBuffer = FileUtil.slice(bigFilebuffer, Math.toIntExact(fileInfo.getLocation()), fileInfo.getSize());
        final ByteBuffer decompressedFileBuffer = new CompressedFile(fileBuffer).decompress();
        if(FileExtension.SSH_EXTENSION.getExtension().equals(fileInfo.getExtension())) {
            BmpFileCreator.create(decompressedFileBuffer, fileInfo.getName(), destinationFolder);
        } else {
            writeToFile(decompressedFileBuffer, fileInfo.getFullName(), destinationFolder);
        }
    }

    private void writeToFile(final ByteBuffer byteBuffer, final String fileName, final Path destinationFolder) throws IOException {
        final Path filePath = FileUtil.prepareDirsAndReturnPath(fileName, destinationFolder);
        FileChannel channel = new FileOutputStream(filePath.toFile()).getChannel();
        byteBuffer.position(0);
        channel.write(byteBuffer);
        channel.close();
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
