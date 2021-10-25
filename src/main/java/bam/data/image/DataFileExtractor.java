package bam.data.image;

import com.mycompany.sshtobpmconverter.BmpFileCreator;
import filecollection.FileExtension;
import image.ssh2.imageheader.ImageTypeTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ByteUtil;
import util.ConverterConfig;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

import static util.FileUtil.createDir;

public class DataFileExtractor {

    private static Logger LOGGER = LoggerFactory.getLogger(DataFileExtractor.class);

    public void extractDataFile(final File file) throws IOException {
        final Path destinationFolder = createDir("data-output");
        final Path filePath = file.toPath();
        final String fileNameWithoutExtension = FileUtil.getNameWithoutExtension(filePath);
        final Path destinationPath = destinationFolder.resolve(fileNameWithoutExtension);
        try (FileChannel fileChannel = FileChannel.open(filePath, EnumSet.of(StandardOpenOption.READ))) {
            final MappedByteBuffer bigFilebuffer = fileChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
            extractDataFile(bigFilebuffer, destinationPath);
        }
    }

    private void extractDataFile(final ByteBuffer buffer, final Path filePath) throws IOException {
        LOGGER.info("-----------------------------------------------------------------------------");
        LOGGER.info("Starting extraction of {}", filePath.toString());
        LOGGER.info("-----------------------------------------------------------------------------");
        try {
            while (buffer.hasRemaining()) {
                BamImageComponent component = new BamImageComponent(buffer);
                if (!component.containsImage()) {
                    throw new UnsupportedEncodingException("Only image components are currently supported");
                }
                if(component.getImageType() == ImageTypeTag.ImageType.LOW_RES_4BPP) {
                    BmpFileCreator.create(component, specifyDestinationPath(filePath, component.getImageNr(), buffer.position()));
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("error extracting " + filePath.toString(), e);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Further extracting of file {} aborted: Encoding of component at position {} is not supported. Caused by: {}", filePath, ByteUtil.printLongWithHex(buffer.position()), e);
        }
    }

    private Path specifyDestinationPath(final Path path, int number, int offset) {
        final String fileName = number + "_" +path.getFileName().toString() + "_" + ConverterConfig.PREFIX +"_" + offset + FileExtension.BMP_EXTENSION.value;
        return path.getParent().resolve(fileName);
    }
}
