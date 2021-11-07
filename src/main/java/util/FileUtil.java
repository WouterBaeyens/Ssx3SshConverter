package util;

import filecollection.FileExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

public class FileUtil {

    public static Stream<File> findFilesInCurrentDirectory(FileExtension fileExtension) throws IOException {
        Stream<Path> paths = Files.find(Paths.get("./"), ConverterConfig.FILE_SEARCH_DEPTH, fileExtension.matcher);
        return paths.filter(Files::isRegularFile).map(Path::toFile);
    }

    public static ByteBuffer attachBuffer(final RandomAccessFile raf) throws IOException {
        final FileChannel ch = raf.getChannel();
        int fileLength = Math.toIntExact(ch.size());
        return ch.map(FileChannel.MapMode.READ_ONLY, 0,
                fileLength);
    }

    /**
     * Some filenames contain slashes, like "data/stuff/default.elf".
     * In this case we need to prepare by creating "data" and "stuff".
     */
    public static Path prepareDirsAndReturnPath(String relativePathToFile,final Path destinationFolder){
        String[] dirs = relativePathToFile.split("[\\\\/]");
        Path currentLocation = destinationFolder;
        for(int i = 0; i < dirs.length -1; i++){
            currentLocation = currentLocation.resolve(dirs[i]);
            createDir(currentLocation);
        }
        return currentLocation.resolve(dirs[dirs.length - 1]);
    }

    public static Path createDir(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdir();
        }
        return directory.toPath();
    }

    public static Path createDir(Path path) {
        File directory = path.toFile();
        if (!directory.exists()) {
            directory.mkdir();
        }
        return directory.toPath();
    }

    public static String getNameWithoutExtension(Path filePath){
        return getNameWithoutExtension(filePath.getFileName().toString());
    }

    public static String getNameWithoutExtension(final String fileName){
        final int indexStartOfExtension = fileName.lastIndexOf(".");
        return indexStartOfExtension > 0 ? fileName.substring(0, indexStartOfExtension) : fileName;
    }

    public static void writeToFile(final ByteBuffer byteBuffer, final String fileName, final Path destinationFolder) throws IOException {
        final Path filePath = FileUtil.prepareDirsAndReturnPath(fileName, destinationFolder);
        FileChannel channel = new FileOutputStream(filePath.toFile()).getChannel();
        byteBuffer.position(0);
        channel.write(byteBuffer);
        channel.close();
    }

    public static ByteBuffer sliceAndSkip(final ByteBuffer buffer, final int size){
        ByteBuffer slicedBuffer = slice(buffer, buffer.position(), size);
        buffer.position(buffer.position() + size);
        return slicedBuffer;
    }

    public static ByteBuffer slice(final ByteBuffer buffer, final int startPosition, final int size){
        final ByteBuffer tmp = buffer.duplicate();
        tmp.position(startPosition);
        tmp.limit(tmp.position() + size);
        return tmp.slice();
    }

    /**
     * Read the data like with absolute positioning. The position is not updated for the next read
     */
    public static byte[] read(RandomAccessFile file, long startPosition, int length) throws IOException {
        byte[] data = new byte[length];
        file.seek(startPosition);
        file.read(data);
        return data;
    }

    /**
     * Reads the data like a stream - updating the position for the next read
     */
    public static byte[] readAndConsume(final ByteBuffer file, final int length) {
        byte[] data = new byte[length];
        file.get(data);
        return data;
    }

    /**
     * Reads the data like a stream until stop-byte is encountered - updating the position for the next read
     */
    public static byte[] readUntilStop(final ByteBuffer file, final byte stopByte) {
        int offset = 0;
        while(offset < file.remaining()){
            final byte tmp = file.get(file.position() + offset);
            offset ++;
            if(tmp == stopByte){
                break;
            }
        }
        byte[] data = new byte[offset];
        file.get(data);
        return data;
    }
}
