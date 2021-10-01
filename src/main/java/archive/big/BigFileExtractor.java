package archive.big;

import com.google.common.io.Files;
import com.mycompany.sshtobpmconverter.BmpFileCreator;
import filecollection.FileExtension;
import util.FileUtil;

import java.io.File;
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
        final ByteBuffer sshFileBuffer = FileUtil.slice(bigFilebuffer, Math.toIntExact(fileInfo.getLocation()), fileInfo.getSize());
        BmpFileCreator.create(sshFileBuffer, fileInfo.getName(), destinationFolder);
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
