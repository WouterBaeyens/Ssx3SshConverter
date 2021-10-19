package bam.data.image;

import archive.big.BigFile;
import archive.big.BigSubFileInfo;
import bam.data.image.header.imagetable.PaddingSizeTag;
import com.mycompany.sshtobpmconverter.BmpFileCreator;
import filecollection.FileExtension;
import util.ByteUtil;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

import static util.FileUtil.createDir;

public class DataFileExtractor {

    public void extractDataFile(final File file) throws IOException {
        final Path destinationFolder = createDir("data-output");
        final Path filePath = file.toPath();
        final String fileNameWithoutExtension = FileUtil.getNameWithoutExtension(filePath);
        final Path destinationPath = destinationFolder.resolve(fileNameWithoutExtension);
        try(FileChannel fileChannel = FileChannel.open(filePath, EnumSet.of(StandardOpenOption.READ))){
            final MappedByteBuffer bigFilebuffer = fileChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
            extractDataFile(bigFilebuffer, destinationPath);
        }
    }

    private void extractDataFile(final ByteBuffer buffer, final Path filePath) throws IOException {
        try {
            int startOfNextComponent = 0;
            while (buffer.hasRemaining()) {
                BamImageComponent component = new BamImageComponent(buffer);
                if (component.containsImage()) {
                    BmpFileCreator.create(component, specifyDestinationPath(filePath, startOfNextComponent));
                    startOfNextComponent += component.getStartOfNextComponent();
                } else {
                    System.out.println("no image found on file " + filePath + " at position " + ByteUtil.printLongWithHex(buffer.position()));
                    break;
                }
            }
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("error extracting "+ filePath.toString(), e);
        }
    }

    private Path specifyDestinationPath(final Path path, int number){
        final String fileName = path.getFileName().toString() + "_" + number + "_1" + FileExtension.BMP_EXTENSION.value;
        return path.getParent().resolve(fileName);
    }
}
